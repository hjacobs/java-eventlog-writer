package de.zalando.util.web.urlmapping;

import com.google.common.collect.ListMultimap;

public interface RequestParamAware {
    boolean appliesTo(ListMultimap<String, String> parameterMap);
}
