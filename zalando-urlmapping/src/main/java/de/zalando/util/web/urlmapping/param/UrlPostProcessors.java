package de.zalando.util.web.urlmapping.param;

import java.util.Map;
import java.util.regex.Pattern;

import de.zalando.util.web.urlmapping.MappingContext;

public final class UrlPostProcessors {
    public static UrlPostProcessor pathInterpolator(final String variableName, final int offset) {
        return new PathInterpolator(variableName, offset);
    }

    private static class PathInterpolator implements UrlPostProcessor {
        private final int originalOffset;
        private final Pattern variablePattern;

        public PathInterpolator(final String variableName, final int originalOffset) {
            this.variablePattern = Pattern.compile("{" + variableName + "}", Pattern.LITERAL);
            this.originalOffset = originalOffset;
        }

        @Override
        public String postProcess(final String url, final MappingContext context,
                final Map<String, String> parameterMap) {
            return variablePattern.matcher(url).replaceFirst(context.getOriginalPathItems().get(originalOffset));
        }
    }
}
