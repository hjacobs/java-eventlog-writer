package de.zalando.zomcat.cxf;

import java.util.List;

/**
 * simple DTO with human readable information about a component's web services. (The same data as rendered on the
 * component's /ws/ HTML page)
 *
 * @author  hjacobs
 */
public class WebServiceOverview {
    private String title;

    private List<WebServiceInfo> webServices;

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public List<WebServiceInfo> getWebServices() {
        return webServices;
    }

    public void setWebServices(final List<WebServiceInfo> webServices) {
        this.webServices = webServices;
    }

}
