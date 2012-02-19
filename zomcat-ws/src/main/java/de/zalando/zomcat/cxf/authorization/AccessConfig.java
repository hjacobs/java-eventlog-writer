package de.zalando.zomcat.cxf.authorization;

import java.util.List;

public interface AccessConfig {

    List<String> getAllowedRoles();

    List<String> getDeniedRoles();

    WebServiceAuthorizationLevel getWebServiceAuthorizationLevel();

}
