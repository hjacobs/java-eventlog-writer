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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
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

import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Overridden CXFServlet with custom service list rendering: The service list additionally contains WSDL documentation
 * and implementor method parameters/return types. To get the improved service list, just use this class instead of the
 * original CXFServlet in web.xml.
 *
 * @author  henning
 */
public class CXFServlet extends org.apache.cxf.transport.servlet.CXFServlet {

    /**
     * helper method to update the destination URLs (using the current context base URL).
     *
     * @param  destinationRegistry
     * @param  request
     */
    protected void updateDestinations(final DestinationRegistry destinationRegistry, final HttpServletRequest request) {

        String base = BaseUrlHelper.getBaseURL(request);

        String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            pathInfo = "/";
        }

        Set<String> paths = destinationRegistry.getDestinationsPaths();
        for (String path : paths) {
            AbstractHTTPDestination d2 = destinationRegistry.getDestinationForPath(path);
            String ad = d2.getEndpointInfo().getAddress();
            if (ad == null && d2.getAddress() != null && d2.getAddress().getAddress() != null) {
                ad = d2.getAddress().getAddress().getValue();
                if (ad == null) {
                    ad = "/";
                }
            }

            if (ad != null && (ad.equals(path))) {

                BaseUrlHelper.setAddress(d2, base + path);
            }
        }
    }

    private void doRenderServiceList(final HttpServletRequest request, final HttpServletResponse response)
        throws IOException {
        DestinationRegistry registry = getDestinationRegistryFromBus(getBus());
        updateDestinations(registry, request);

        AbstractDestination[] destinations = registry.getSortedDestinations();
        response.setContentType("text/html; charset=UTF-8");

        PrintWriter writer = response.getWriter();
        writer.write("<html><head><meta http-equiv=content-type content=\"text/html; charset=UTF-8\">");
        writer.write("<title>CXF - Service list</title>");
        writer.write("<style>");
        writer.write("html { font: 13px Arial, Helvetica, sans-serif; }");
        writer.write(
            "h2 { font-size: 16px; margin: 16px 0 4px 0; padding: 0 0 4px 0; border-bottom: 4px solid #eee; }");
        writer.write("h2 a { font-size: 12px; margin-left: 24px; }");
        writer.write(".doc { color: #888; font-style: italic; }");
        writer.write("p { margin: 4px 0; }");
        writer.write("ul { margin: 0 0 16px 8px; padding: 0; list-style: none;}");
        writer.write("li { margin: 3px 0; padding: 0 0 3px 0; border-bottom: 1px dotted #ccc;}");
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
        writer.write("<p>Available SOAP services:</p>");

        boolean collapsed = destinations.length > 1;
        for (AbstractDestination dest : destinations) {
            writeSoapEndpoint(writer, dest, collapsed);
        }

        writer.write("</body>");
    }

    protected static class OperationInfoComparator implements Comparator<OperationInfo> {

        @Override
        public int compare(final OperationInfo a, final OperationInfo b) {
            return a.getName().getLocalPart().compareTo(b.getName().getLocalPart());
        }

    }

    protected static class OperationParameter {
        public String name;
        public Type type;

        public OperationParameter(final String name, final Type type) {
            this.name = name;
            this.type = type;
        }
    }

    private static final OperationInfoComparator OPERATION_INFO_COMPARATOR = new OperationInfoComparator();

    private static String getTypeString(final Type type) {
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            StringBuilder sb = new StringBuilder(getTypeString(pType.getRawType()));
            sb.append('<');

            int i = 0;
            for (Type t : pType.getActualTypeArguments()) {
                if (i > 0) {
                    sb.append(", ");
                }

                sb.append(getTypeString(t));
                i++;
            }

            sb.append('>');
            return sb.toString();
        } else if (type instanceof Class) {
            return ((Class) type).getSimpleName();
        }

        return null;
    }

    private static String getDocumentationAsHtml(final String doc) {
        return StringEscapeUtils.escapeXml(doc).replace("\n", "<br />");
    }

    private void writeSoapEndpoint(final PrintWriter writer, final AbstractDestination sd, final boolean collapsed) {
        final EndpointInfo ei = sd.getEndpointInfo();
        final String address = ei.getAddress();
        final String name = ei.getInterface().getName().getLocalPart();

        final Map<String, List<OperationParameter>> operationParameters = Maps.newHashMap();
        final Map<String, Type> operationReturnTypes = Maps.newHashMap();

        // workaround to get implementor class: relies on naming convention in cxf.xml
        // (e.g. "MyExampleWebService" needs to have "myExampleWebService" implementor bean)
        Object implementor = null;
        try {
            implementor = getBus().getExtension(ApplicationContext.class).getBean(name.substring(0, 1).toLowerCase()
                        + name.substring(1));
        } catch (NoSuchBeanDefinitionException e) {
            // probably our assumption (naming convention) was incorrect and the bean was not found with the assumed
            // name
            // => ignore the "bean not found" error
        }

        if (implementor != null) {
            final Class clazz = implementor.getClass();
            Method[] methods = clazz.getMethods();

            List<OperationParameter> params;

            int pos;
            for (final Method method : methods) {
                operationReturnTypes.put(method.getName(), method.getGenericReturnType());
                params = Lists.newArrayList();

                pos = 0;
                for (final Type t : method.getGenericParameterTypes()) {
                    params.add(new OperationParameter("arg" + pos, t));
                    pos++;
                }

                pos = 0;
                for (final Annotation[] as : method.getParameterAnnotations()) {
                    for (final Annotation a : as) {
                        if (a instanceof WebParam) {
                            params.get(pos).name = ((WebParam) a).name();
                        }
                    }

                    pos++;
                }

                operationParameters.put(method.getName(), params);
            }

            // also scan interface annotations
            for (Class intface : clazz.getInterfaces()) {
                methods = intface.getMethods();
                for (final Method method : methods) {
                    params = operationParameters.get(method.getName());
                    if (params == null) {
                        continue;
                    }

                    pos = 0;
                    for (final Annotation[] as : method.getParameterAnnotations()) {
                        for (final Annotation a : as) {
                            if (a instanceof WebParam) {
                                params.get(pos).name = ((WebParam) a).name();
                            }
                        }

                        pos++;
                    }
                }
            }
        }

        final List<OperationInfo> operations = Lists.newArrayList(ei.getInterface().getOperations());

        // sort operations (methods) by name
        Collections.sort(operations, OPERATION_INFO_COMPARATOR);

        writer.write("<h2>" + name + " ");
        writer.write("<a href=\"" + address + "?wsdl\">WSDL</a>");
        if (collapsed) {
            writer.write("<a href=\"javascript:void toggleOperations('" + name + "-ops')\">Operations ("
                    + operations.size() + ")</a>");
        }

        writer.write("</h2>");
        if (ei.getInterface().getDocumentation() != null) {
            writer.write("<p class=\"doc\">" + getDocumentationAsHtml(ei.getInterface().getDocumentation()) + "</p>");
        }

        if (ei.getService().getDocumentation() != null) {
            writer.write("<p class=\"doc\">" + getDocumentationAsHtml(ei.getService().getDocumentation()) + "</p>");
        }

        writer.write("<ul id=\"" + name + "-ops\"");
        if (collapsed) {
            writer.write(" style=\"display:none\"");
        }

        writer.write(">");

        for (OperationInfo oi : operations) {
            if (oi.getProperty("operation.is.synthetic") != Boolean.TRUE) {
                String localName = oi.getName().getLocalPart();
                writer.write("<li><strong>" + localName + "</strong>");

                List<OperationParameter> params = operationParameters.get(localName);
                if (params != null) {
                    writer.write(" (");

                    int i = 0;
                    for (OperationParameter param : params) {
                        if (i > 0) {
                            writer.write(", ");
                        }

                        writer.write("<em>" + StringEscapeUtils.escapeXml(getTypeString(param.type)) + "</em>");
                        writer.write(" ");
                        writer.write(param.name);
                        i++;
                    }

                    writer.write(")");
                }

                Type returnType = operationReturnTypes.get(localName);
                if (returnType != null) {
                    writer.write(" : ");
                    writer.write("<em>" + StringEscapeUtils.escapeXml(getTypeString(returnType)) + "</em>");
                }

                if (oi.getDocumentation() != null) {
                    writer.write("<p class=\"doc\">" + getDocumentationAsHtml(oi.getDocumentation()) + "</p>");
                }

                writer.write("</li>");
            }
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
        ClassLoader origLoader = Thread.currentThread().getContextClassLoader();
        try {

            ClassLoader loader = getBus().getExtension(ClassLoader.class);
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
        DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
        try {
            DestinationFactory df = dfm.getDestinationFactory("http://cxf.apache.org/transports/http/configuration");
            if (df instanceof HTTPTransportFactory) {
                HTTPTransportFactory transportFactory = (HTTPTransportFactory) df;
                return transportFactory.getRegistry();
            }
        } catch (BusException e) {
            // why are we throwing a busexception if the DF isn't found?
        }

        return null;
    }

    @Override
    protected void invoke(final HttpServletRequest request, final HttpServletResponse response)
        throws ServletException {
        String pathInfo = request.getPathInfo() == null ? "" : request.getPathInfo();
        if (StringUtils.isBlank(pathInfo) || "/".equals(pathInfo)) {
            try {
                renderServiceList(request, response);
            } catch (IOException ex) {
                throw new ServletException("Could not render service list", ex);
            }
        } else {
            super.invoke(request, response);
        }

    }
}
