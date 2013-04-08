package de.zalando.zomcat.cxf;

import java.io.IOException;
import java.io.PrintWriter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebParam;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
import org.apache.cxf.service.model.BindingInfo;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.OperationInfo;
import org.apache.cxf.transport.AbstractDestination;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.http.AbstractHTTPDestination;
import org.apache.cxf.transport.http.DestinationRegistry;
import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.apache.cxf.transport.servlet.BaseUrlHelper;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import org.springframework.cglib.proxy.Enhancer;

import org.springframework.context.ApplicationContext;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.google.gson.Gson;

/**
 * Overridden CXFServlet with custom service list rendering: The service list additionally contains WSDL documentation
 * and implementor method parameters/return types. To get the improved service list, just use this class instead of the
 * original CXFServlet in web.xml.
 *
 * @author  henning
 */
public class CXFServlet extends org.apache.cxf.transport.servlet.CXFServlet {

    private static final long serialVersionUID = -5792596996273480173L;

    // URL to source code browser (e.g. OpenGrok)
    // %1$s is replaced with service name
    // %2$s is replaced with method name
    private static final String SOURCE_LINK =
        "https://opengrok.zalando.net/search?q=%2$s&project=reboot&defs=%1$s&refs=&path=&hist=";

    /**
     * helper method to update the destination URLs (using the current context base URL).
     *
     * @param  destinationRegistry
     * @param  request
     */
    protected void updateDestinations(final DestinationRegistry destinationRegistry, final HttpServletRequest request) {

        final String base = BaseUrlHelper.getBaseURL(request);

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        final Set<String> paths = destinationRegistry.getDestinationsPaths();
        for (final String path : paths) {
            final AbstractHTTPDestination d2 = destinationRegistry.getDestinationForPath(path);
            String ad = d2.getEndpointInfo().getAddress();
            if (ad == null && d2.getAddress() != null && d2.getAddress().getAddress() != null) {
                ad = d2.getAddress().getAddress().getValue();

                if (ad == null) {
                    ad = "/";
                }
            }

            if (ad != null && ad.equals(path)) {

                BaseUrlHelper.setAddress(d2, base + path);
            }
        }
    }

    /**
     * @param   sd
     *
     * @return
     */
    private WebServiceInfo getWebServiceInfo(final AbstractDestination sd) {

        final EndpointInfo ei = sd.getEndpointInfo();
        final BindingInfo binding = ei.getBinding();

        final String address = ei.getAddress();
        final QName qname = ei.getInterface() == null ? ei.getName() : ei.getInterface().getName();
        final String name = qname == null ? "" : qname.getLocalPart();

        final WebServiceInfo info = new WebServiceInfo();
        info.setName(name);
        info.setAddress(address);
        info.setRest(binding.getBindingId().contains("jaxrs"));

        // workaround to get implementor class: relies on naming convention in cxf.xml
        // (e.g. "MyExampleWebService" needs to have "myExampleWebService" implementor bean)
        Object implementor = null;
        try {
            implementor = getBus().getExtension(ApplicationContext.class).getBean(name.substring(0, 1).toLowerCase()
                        + name.substring(1));
        } catch (final NoSuchBeanDefinitionException e) {
            // probably our assumption (naming convention) was incorrect and the bean was not found with the assumed
            // name
            // => ignore the "bean not found" error
        }

        final Map<String, WebServiceInfo.OperationInfo> operationInformations = getOperationInformations(implementor);

        info.setDocumentation(getDocumentation(ei));

        if (ei.getInterface() != null && ei.getInterface().getOperations() != null) {
            final List<OperationInfo> operations = Lists.newArrayList(ei.getInterface().getOperations());

            final List<WebServiceInfo.OperationInfo> ops = Lists.newArrayList();
            for (final OperationInfo oi : operations) {
                if (oi.getProperty("operation.is.synthetic") != Boolean.TRUE) {
                    final String localName = oi.getName().getLocalPart();

                    WebServiceInfo.OperationInfo op = operationInformations.get(localName);

                    if (op == null) {

                        // implementor was not found => we do not have any method details
                        op = new WebServiceInfo.OperationInfo();
                        op.setName(localName);
                    }

                    if (oi.getDocumentation() != null) {
                        op.setDocumentation(oi.getDocumentation());
                    }

                    ops.add(op);
                }

            }

            // sort operations (methods) by name
            Collections.sort(ops, OPERATION_INFO_COMPARATOR);

            info.setOperations(ops);

        } else if (info.isRest() && implementor != null) {

            final List<WebServiceInfo.OperationInfo> ops = Lists.newArrayList();
            for (final Method m : getRealClass(implementor).getMethods()) {

                if (m.isAnnotationPresent(GET.class) || m.isAnnotationPresent(POST.class)
                        || m.isAnnotationPresent(HEAD.class) || m.isAnnotationPresent(PUT.class)
                        || m.isAnnotationPresent(DELETE.class)) {
                    final WebServiceInfo.OperationInfo op = operationInformations.get(m.getName());
                    if (m.isAnnotationPresent(Path.class)) {
                        final String path = m.getAnnotation(Path.class).value();
                        op.setRestPath(path);
                    }

                    ops.add(op);
                }
            }

            // sort operations (methods) by name
            Collections.sort(ops, OPERATION_INFO_COMPARATOR);

            info.setOperations(ops);
        }

        return info;
    }

    /**
     * @param   implementor
     *
     * @return
     */
    private Class<? extends Object> getRealClass(final Object implementor) {
        if (Enhancer.isEnhanced(implementor.getClass())) {
            return implementor.getClass().getSuperclass();
        }

        return implementor.getClass();
    }

    /**
     * get documentation string from endpoint and service.
     *
     * @param   ei
     *
     * @return
     */
    private String getDocumentation(final EndpointInfo ei) {
        final StringBuilder doc = new StringBuilder();
        if (ei.getInterface() != null && ei.getInterface().getDocumentation() != null) {
            doc.append(ei.getInterface().getDocumentation());

        }

        if (ei.getService() != null && ei.getService().getDocumentation() != null) {
            if (doc.length() > 0) {
                doc.append('\n');
            }

            doc.append(ei.getService().getDocumentation());
        }

        return doc.toString();
    }

    /**
     * retrieve method details (return types, parameters) from implementor class using reflection.
     *
     * @param   implementor
     *
     * @return
     */
    private Map<String, WebServiceInfo.OperationInfo> getOperationInformations(final Object implementor) {
        final Map<String, WebServiceInfo.OperationInfo> operationInformations = Maps.newHashMap();

        if (implementor == null) {
            return operationInformations;
        }

        final Class<? extends Object> clazz = getRealClass(implementor);
        Method[] methods = clazz.getMethods();

        for (final Method method : methods) {
            final List<WebServiceInfo.OperationParameter> params = Lists.newArrayList();

            int pos = 0;
            for (final Type t : method.getGenericParameterTypes()) {
                final WebServiceInfo.OperationParameter param = new WebServiceInfo.OperationParameter();
                param.setName("arg" + pos);
                param.setType(getTypeString(t));
                params.add(param);
                pos++;
            }

            updateNameFromAnnotations(method.getParameterAnnotations(), params);

            final WebServiceInfo.OperationInfo info = new WebServiceInfo.OperationInfo();
            info.setName(method.getName());
            info.setReturnType(getTypeString(method.getGenericReturnType()));
            info.setParameters(params);
            operationInformations.put(method.getName(), info);
        }

        // also scan interface annotations
        for (final Class<?> intface : clazz.getInterfaces()) {
            methods = intface.getMethods();
            for (final Method method : methods) {
                final List<WebServiceInfo.OperationParameter> params = operationInformations.get(method.getName())
                                                                                            .getParameters();
                if (params == null) {
                    continue;
                }

                updateNameFromAnnotations(method.getParameterAnnotations(), params);
            }
        }

        return operationInformations;
    }

    /**
     * @param  annotations
     * @param  params
     */
    private void updateNameFromAnnotations(final Annotation[][] annotations,
            final List<WebServiceInfo.OperationParameter> params) {
        int pos;
        pos = 0;
        for (final Annotation[] as : annotations) {
            for (final Annotation a : as) {
                if (a instanceof WebParam && params.size() > pos) {
                    params.get(pos).setName(((WebParam) a).name());
                }
            }

            pos++;
        }
    }

    /**
     * @return
     */
    private String getTitle() {
        final ServletContext application = getServletConfig().getServletContext();
        return application.getServletContextName();
    }

    /**
     * @param   request
     * @param   response
     *
     * @throws  IOException
     */
    private void doRenderServiceList(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        final DestinationRegistry registry = getDestinationRegistryFromBus(getBus());
        updateDestinations(registry, request);

        final AbstractDestination[] destinations = registry.getSortedDestinations();

        final boolean json = request.getParameterMap().containsKey("view");

        final List<WebServiceInfo> webServices = Lists.newArrayList();
        final WebServiceOverview overview = new WebServiceOverview();
        overview.setTitle(getTitle());
        overview.setWebServices(webServices);

        for (final AbstractDestination dest : destinations) {
            webServices.add(getWebServiceInfo(dest));
        }

        final PrintWriter writer = response.getWriter();
        if (json) {
            response.setContentType("text/plain; charset=UTF-8");

            final Gson gson = new Gson();
            writer.print(gson.toJson(overview));

        } else {
            response.setContentType("text/html; charset=UTF-8");

            writer.write("<html><head><meta http-equiv=content-type content=\"text/html; charset=UTF-8\">");
            writer.write("<title>CXF - Service list for " + overview.getTitle() + "</title>");
            writer.write("<style>");
            writer.write("html { font: 13px Arial, Helvetica, sans-serif; }");
            writer.write(
                "h2 { font-size: 16px; margin: 16px 0 4px 0; padding: 0 0 4px 0; border-bottom: 4px solid #eee; }");
            writer.write("h2 a { font-size: 12px; margin-left: 24px; }");
            writer.write(".doc { color: #888; font-style: italic; }");
            writer.write(".restpath { color: darkBlue; }");
            writer.write("p { margin: 4px 0; }");
            writer.write("ul { margin: 0 0 16px 8px; padding: 0; list-style: none;}");
            writer.write("li { margin: 3px 0; padding: 0 0 3px 0; border-bottom: 1px dotted #ccc;}");
            writer.write("li a { font-weight: bold; color: #000; text-decoration: none; }");
            writer.write("li a:hover { color: #333; text-decoration: underline; }");
            writer.write("li p { margin: 0; }");
            writer.write("li em { color: #336; font-style: normal; }");

            // writer.write("ul { color: #888; }");
            writer.write("</style>");
            writer.write("</head><body>");
            writer.write("<script>");
            writer.write("function toggleOperations(id) { var elem = document.getElementById(id);"
                    + "if (elem.style.display == 'block') { elem.style.display='none'; } else { elem.style.display='block'; }"
                    + "}");
            writer.write("</script>");
            writer.write("<p>Available SOAP services for <strong>" + overview.getTitle() + "</strong>:</p>");

            final boolean collapsed = destinations.length > 1;
            for (final WebServiceInfo info : webServices) {
                writeSoapEndpoint(writer, info, collapsed);
            }

            writer.write("</body>");
        }
    }

    protected static class OperationInfoComparator implements Comparator<WebServiceInfo.OperationInfo> {

        @Override
        public int compare(final WebServiceInfo.OperationInfo a, final WebServiceInfo.OperationInfo b) {
            return a.getName().compareTo(b.getName());
        }

    }

    private static final OperationInfoComparator OPERATION_INFO_COMPARATOR = new OperationInfoComparator();

    /**
     * @param   type
     *
     * @return
     */
    private static String getTypeString(final Type type) {
        if (type == null) {
            return null;
        }

        if (type instanceof ParameterizedType) {
            final ParameterizedType pType = (ParameterizedType) type;
            final StringBuilder sb = new StringBuilder(getTypeString(pType.getRawType()));
            sb.append('<');

            int i = 0;
            for (final Type t : pType.getActualTypeArguments()) {
                if (i > 0) {
                    sb.append(", ");
                }

                sb.append(getTypeString(t));
                i++;
            }

            sb.append('>');
            return sb.toString();
        } else if (type instanceof Class) {
            return ((Class<?>) type).getSimpleName();
        }

        return null;
    }

    /**
     * @param   doc
     *
     * @return
     */
    private static String getDocumentationAsHtml(final String doc) {
        return StringEscapeUtils.escapeXml(doc).replace("\n", "<br />");
    }

    /**
     * @param  writer
     * @param  ws
     * @param  collapsed
     */
    private void writeSoapEndpoint(final PrintWriter writer, final WebServiceInfo ws, final boolean collapsed) {

        writer.write("<h2>" + ws.getName() + " ");

        if (ws.isRest()) {
            writer.write("<a href=\"#\">REST</a>");
        } else {
            writer.write("<a href=\"" + ws.getAddress() + "?wsdl\">WSDL</a>");
        }

        if (collapsed) {
            writer.write("<a href=\"javascript:void toggleOperations('" + ws.getName() + "-ops')\">Operations ("
                    + ws.getOperations().size() + ")</a>");
        }

        writer.write("</h2>");
        if (!Strings.isNullOrEmpty(ws.getDocumentation())) {
            writer.write("<p class=\"doc\">" + getDocumentationAsHtml(ws.getDocumentation()) + "</p>");
        }

        writer.write("<ul id=\"" + ws.getName() + "-ops\"");
        if (collapsed) {
            writer.write(" style=\"display:none\"");
        }

        writer.write(">");

        for (final WebServiceInfo.OperationInfo oi : ws.getOperations()) {
            final String localName = oi.getName();

            writer.write("<li><a href=\"" + String.format(SOURCE_LINK, ws.getName(), localName) + "\">" + localName
                    + "</a>");

            if (oi.getParameters() != null) {
                writer.write(" (");

                int i = 0;
                for (final WebServiceInfo.OperationParameter param : oi.getParameters()) {
                    if (i > 0) {
                        writer.write(", ");
                    }

                    writer.write("<em>" + StringEscapeUtils.escapeXml(param.getType()) + "</em>");
                    writer.write(" ");
                    writer.write(param.getName());
                    i++;
                }

                writer.write(")");
            }

            if (oi.getReturnType() != null) {
                writer.write(" : ");
                writer.write("<em>" + StringEscapeUtils.escapeXml(oi.getReturnType()) + "</em>");
            }

            if (ws.isRest() && oi.getRestPath() != null) {
                final String path = ws.getAddress().concat("/").concat(oi.getRestPath());
                writer.write("<p><span>Path: </span><span class=\"restpath\">" + path + "</span></p>");
            }

            if (oi.getDocumentation() != null) {
                writer.write("<p class=\"doc\">" + getDocumentationAsHtml(oi.getDocumentation()) + "</p>");
            }

            writer.write("</li>");
        }

        writer.write("</ul>");
    }

    /**
     * wrapper method to correctly initialize CXF Bus context (copied from CXFNonSpringServlet.invoke()).
     *
     * @param  request
     * @param  response
     */
    private void renderServiceList(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        final ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
        try {

            final ClassLoader loader = getBus().getExtension(ClassLoader.class);
            if (loader != null) {
                Thread.currentThread().setContextClassLoader(loader);
            }

            BusFactory.setThreadDefaultBus(getBus());
            doRenderServiceList(request, response);
        } finally {
            BusFactory.setThreadDefaultBus(null);
            Thread.currentThread().setContextClassLoader(origLoader);
        }
    }

    /**
     * this method is an exact copy of
     * org.apache.cxf.transport.servlet.CXFNonSpringServlet.getDestinationRegistryFromBus().
     *
     * @param   bus
     *
     * @return
     */
    private static DestinationRegistry getDestinationRegistryFromBus(final Bus bus) {
        final DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
        try {
            final DestinationFactory df = dfm.getDestinationFactory(
                    "http://cxf.apache.org/transports/http/configuration");
            if (df instanceof HTTPTransportFactory) {
                final HTTPTransportFactory transportFactory = (HTTPTransportFactory) df;
                return transportFactory.getRegistry();
            }
        } catch (final BusException e) {
            // why are we throwing a busexception if the DF isn't found?
        }

        return null;
    }

    @Override
    protected void invoke(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException {
        final String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
        if (StringUtils.isBlank(pathInfo) || "/".equals(pathInfo)) {
            try {
                renderServiceList(request, response);
            } catch (final IOException ex) {
                throw new ServletException("Could not render service list", ex);
            }
        } else {
            super.invoke(request, response);
        }

    }
}
