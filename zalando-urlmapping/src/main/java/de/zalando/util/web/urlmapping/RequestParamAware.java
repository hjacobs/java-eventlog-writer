package de.zalando.util.web.urlmapping;

import java.util.Map;

public interface RequestParamAware {
    boolean appliesTo(Map<String, String[]> parameterMap);
}
