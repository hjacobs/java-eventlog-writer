/**
 *
 */
package de.zalando.util.web.urlmapping.rule;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.base.Strings.repeat;
import static com.google.common.collect.Iterables.skip;
import static com.google.common.collect.Lists.newArrayList;

import static de.zalando.util.web.urlmapping.util.Delimiter.COMMA;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import java.text.MessageFormat;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Joiner.MapJoiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.io.CharStreams;
import com.google.common.io.InputSupplier;
import com.google.common.io.LineProcessor;
import com.google.common.io.OutputSupplier;

import de.zalando.util.web.urlmapping.RuleContext;
import de.zalando.util.web.urlmapping.builder.PathBuilder;
import de.zalando.util.web.urlmapping.domain.MappingConstants;
import de.zalando.util.web.urlmapping.param.Handler;
import de.zalando.util.web.urlmapping.param.PathParamHandler;
import de.zalando.util.web.urlmapping.param.PathParamHandlers;
import de.zalando.util.web.urlmapping.param.PostProcessors;
import de.zalando.util.web.urlmapping.param.RequestParamHandler;
import de.zalando.util.web.urlmapping.param.RequestParamHandlers;
import de.zalando.util.web.urlmapping.util.Delimiter;
import de.zalando.util.web.urlmapping.util.Helper;

/**
 * This class handles serialization and deserialization of rulesets.
 *
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 */
public class RuleSetDescription {

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof RuleSetDescription) {
            final RuleSetDescription other = (RuleSetDescription) obj;
            return Objects.equal(id, other.id) && Objects.equal(getTargetUrl(), other.getTargetUrl())
                    && Objects.equal(paths, other.paths) && Objects.equal(parameters, other.parameters);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, getTargetUrl(), paths, parameters);
    }

    @Override
    public String toString() {

        return Objects.toStringHelper(this).add("id", id).add("targetUrl", getTargetUrl()).add("paths", paths)
                      .add("parameters", parameters).toString();
    }

    static class Deserializer implements LineProcessor<List<RuleSetDescription>> {

        private final List<RuleSetDescription> rules = newArrayList();
        private RuleSetDescription current;

        @Override
        public boolean processLine(final String line) throws IOException {

            final String strippedLine = CharMatcher.WHITESPACE.removeFrom(line);
            if (strippedLine.startsWith(RULE_PREFIX)) {
                final String id = strippedLine.substring(RULE_PREFIX.length());
                checkArgument(!id.isEmpty(), "Bad rule definition (Rule must have an id): ", line);
                current = new RuleSetDescription(id);
                rules.add(current);
            } else if (strippedLine.startsWith(PARAM_PREFIX)) {
                checkArgument(current != null,
                    "Bad Parameter definition (Parameters can only be defined inside a rule: ", line);
                current.prepareParams().add(Parameter.deserialize(strippedLine.substring(PARAM_PREFIX.length())));
            } else if (strippedLine.startsWith(PATH_PREFIX)) {
                checkArgument(current != null, "Bad path definition: paths can only be defined inside a rule: ", line);

                final String path = strippedLine.substring(PATH_PREFIX.length());
                checkArgument(MappingConstants.ALLOWED_PATH_CHARACTERS.matchesAllOf(path),
                    "mapping path contains illegal characters: ", line);
                current.paths.add(path);
            } else if (strippedLine.startsWith(TARGET_PREFIX)) {
                checkArgument(current != null,
                    "Bad target URL definition: target URL can only be defined inside a rule: ", line);
                current.setTargetUrl(strippedLine.substring(TARGET_PREFIX.length()));
            }

            return true;
        }

        @Override
        public List<RuleSetDescription> getResult() {
            for (final RuleSetDescription rsd : rules) {
                rsd.checkIntegrity();
                rsd.sortParams();
            }

            return rules;
        }

    }

    private static final String PATH_PREFIX = "PATH:";
    private static final String RULE_PREFIX = "RULE:";
    private static final String TARGET_PREFIX = "TARGET:";
    private static final String PARAM_PREFIX = "PARAM:";
    private static final String NEWLINE = "\n";
    private static final String INDENT = "  ";

    private static final MapJoiner MAP_JOINER = Joiner.on(';').withKeyValueSeparator("=");

    public RuleSetDescription(final String id) {
        this.id = id;
    }

    private static final Ordering<Parameter> PARAMETER_ORDERING = new Ordering<Parameter>() {
//J-
        @Override
        public int compare(final Parameter left, final Parameter right) {
            return ComparisonChain.start()
                                  .compareTrueFirst(right.isAnyTypeOfPathParam(), left.isAnyTypeOfPathParam())
                                  .compareFalseFirst(left.optional, right.optional)
                                  .result();
        }
//J+
    };

    public void sortParams() {

        // path params before non-path params
        Collections.sort(parameters, PARAMETER_ORDERING);
    }

    public void checkIntegrity() {
        checkState(id != null, "No Id defined in rule");
        checkState(!Strings.isNullOrEmpty(getTargetUrl()), "No target URL defined in rule");
        checkState(!paths.isEmpty(), "No paths defined in rule");

        int lastLength = -1;

        for (final String path : paths) {
            final int thisLength = calculatePathLength(path);
            if (lastLength < 0) {
                lastLength = thisLength;
            } else {
                checkState(thisLength == lastLength, "Rule contains paths of different lengths: ", paths);
            }
        }
    }

    private static int calculatePathLength(final String path) {
        return ImmutableList.copyOf(splitPath(path)).size();
    }

    private static Iterable<String> splitPath(final String path) {
        return Delimiter.SLASH.trimmedSplitter().split(path);
    }

    private final String id;

    enum ParamKey {
        NAME,
        PREFIX,
        SUFFIX,
        REQUESTPARAM,
        OPTIONAL,
        AGGREGATE,
        FIXEDVALUE,
        FIRST,
        PATHVARIABLE,
        SEO;

        public static ParamKey get(final String key) {
            for (final ParamKey candidate : values()) {
                if (candidate.name().equalsIgnoreCase(key)) {
                    return candidate;
                }
            }

            return null;
        }
    }

    private List<Parameter> parameters = ImmutableList.of();
    private final Set<String> paths = Sets.newTreeSet();
    private String targetUrl;

    /**
     * Add a base path to the rule.
     */
    public RuleSetDescription addPath(final String path) {
        paths.add(path);
        return this;
    }

    private List<Parameter> prepareParams() {
        if (parameters.isEmpty()) {
            parameters = newArrayList();
        }

        return parameters;
    }

    /**
     * Add a parameter mapping from an incoming request parameter to an outgoing one.
     */
    public RuleSetDescription addRequestParameter(final String incoming, final String outgoing) {
        final Parameter param = new Parameter();
        param.name = outgoing;
        param.incomingName = singletonList(incoming);
        prepareParams().add(param);
        return this;
    }

    /**
     * Add a path parameter mapping with a suffix.
     *
     * @param  name    the name of the outgoing parameter
     * @param  suffix  the suffix (must be present on the incoming path, will be removed from the outgoing parameter)
     */
    public RuleSetDescription addPathParameterWithSuffix(final String name, final String suffix) {
        return addPathParameter(name, null, suffix, false);
    }

    /**
     * Add a path parameter mapping with a prefix.
     *
     * @param  name    the name of the outgoing parameter
     * @param  prefix  the prefix (must be present on the incoming path, will be removed from the outgoing parameter)
     */
    public RuleSetDescription addPathParameterWithPrefix(final String name, final String prefix) {
        return addPathParameter(name, prefix, null, false);
    }

    /**
     * Add an optional path parameter.
     *
     * @param  name  the name of the outgoing parameter
     */
    public RuleSetDescription addOptionalPathParameter(final String name) {
        return addPathParameter(name, null, null, true);
    }

    /**
     * Add a path parameter with the specified outgoing name.
     *
     * <ul>
     *   <li>If prefix is not null, the prefix will be required in the incoming path and stripped from the outgoing
     *     parameter.</li>
     *   <li>If suffix is not null, the suffix will be required in the incoming path and stripped from the outgoing
     *     parameter.</li>
     *   <li>If optional is true, the path parameter is optional (a match will occur even if it isn't present)</li>
     * </ul>
     */
    public RuleSetDescription addPathParameter(final String name, @Nullable final String prefix,
            @Nullable final String suffix, final boolean optional) {
        final Parameter param = new Parameter();
        param.name = name;
        param.suffix = suffix;
        param.prefix = prefix;
        param.optional = optional;
        prepareParams().add(param);
        return this;
    }

    /**
     * Add a request parameter aggregation: multiple incoming parameters will be mapped into one outgoing parameter
     * using the specified delimiter.
     */
    public RuleSetDescription addAggregationParameter(final String outgoingName, final char delimiter,
            final List<String> incomingParameters) {
        checkArgument(incomingParameters.size() > 1,
            "Error mapping %s to %s. Aggregations must have more than one incoming parameter.", incomingParameters,
            outgoingName);

        final Parameter param = new Parameter();
        param.name = outgoingName;
        param.delimiter = delimiter;
        param.incomingName = ImmutableList.copyOf(incomingParameters);
        prepareParams().add(param);
        return this;
    }

    /**
     * Register this Rule Description with a RuleContext Builder.
     */
    public void register(final RuleContext.Builder contextBuilder) {
        checkIntegrity();

        final int length = calculatePathLength(Iterables.get(paths, 0));
        sortParams();

        final List<Handler> allHandlers = ImmutableList.copyOf(Iterables.transform(parameters,
                    Parameter.HANDLER_FUNCTION));
        final MappingRule rule = new ForwardMappingRule(id, getTargetUrl(), length, allHandlers);
        for (final String basePath : paths) {
            final PathBuilder builder = new PathBuilder();
            for (final String pathItem : splitPath(basePath)) {
                builder.add(pathItem);
            }

            for (final Parameter parameter : parameters) {
                if (parameter.isPathParam() || parameter.isVariablePathParam() || parameter.isSeoParameter()) {
                    if (parameter.optional) {
                        builder.addOptionalWildCard();
                    } else {
                        builder.addWildCard();
                    }
                }
            }

            contextBuilder.addRule(builder.build(), rule);
        }
    }

    private static class Parameter {
        private static final String TRUE = Boolean.TRUE.toString();

        public static final Function<Parameter, Handler> HANDLER_FUNCTION =
            new Function<RuleSetDescription.Parameter, Handler>() {

                @Override
                public Handler apply(final Parameter input) {
                    return input.toHandler();
                }
            };

        private String name;
        private List<String> incomingName = emptyList();
        private Character delimiter;
        private String suffix;
        private String prefix;
        private boolean optional;
        private boolean first;

        public String value;

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof RuleSetDescription.Parameter) {
                final RuleSetDescription.Parameter other = (RuleSetDescription.Parameter) obj;
                return Objects.equal(name, other.name) && Objects.equal(incomingName, other.incomingName)
                        && Objects.equal(prefix, other.prefix) && Objects.equal(suffix, other.suffix)
                        && Objects.equal(delimiter, other.delimiter) && Objects.equal(optional, other.optional)
                        && Objects.equal(first, other.first);
            } else {
                return false;
            }
        }

        static Parameter deserialize(final String line) {
            final Set<Entry<String, String>> entrySet = Helper.splitMap(line, '=', ';').entrySet();
            final Parameter parameter = new Parameter();

            for (final Entry<String, String> entry : entrySet) {
                final String key = entry.getKey();
                final ParamKey paramKey = ParamKey.get(key);
                checkArgument(paramKey != null, "Unknown token '%s' in line %s", key, line);

                final String value = entry.getValue();
                switch (paramKey) {

                    case AGGREGATE :
                        parameter.delimiter = value.charAt(0);
                        parameter.incomingName = ImmutableList.copyOf(COMMA.splitter().split(value.substring(1)));
                        break;

                    case NAME :
                        parameter.name = value;
                        break;

                    case OPTIONAL :
                        parameter.optional = Boolean.parseBoolean(value);
                        break;

                    case PREFIX :
                        parameter.prefix = emptyToNull(value);
                        break;

                    case REQUESTPARAM :
                        parameter.incomingName = ImmutableList.of(value);
                        break;

                    case SUFFIX :
                        parameter.suffix = emptyToNull(value);
                        break;

                    case FIXEDVALUE :
                        parameter.value = value;
                        break;

                    case SEO :
                        parameter.name = SEO;
                        break;

                    case PATHVARIABLE :
                        parameter.name = FROM_INPUT;
                        parameter.value = value;
                        break;

                    case FIRST :
                        parameter.first = Boolean.parseBoolean(value);
                        break;

                    default :
                        throw new IllegalArgumentException("Type not implemented: " + paramKey);
                }
            }

            try {
                parameter.checkIntegrity();
            } catch (final IllegalStateException e) {
                throw new IllegalStateException(MessageFormat.format("Error in Line ''{0}'': ''{1}''", line,
                        e.getMessage()));
            }

            return parameter;
        }

        private void checkIntegrity() {
            if (!isFixedParam() && !isSeoParameter()) {
                checkState(!Strings.isNullOrEmpty(name), "Parameter name missing");
            }

            if (isSeoParameter()) {
                checkState(value == null, "Value not allowed for SEO parameters");
            }

            if (isVariablePathParam() || isFixedParam() || isPathParam() || isSeoParameter()) {
                checkState(incomingName.isEmpty(), "Incoming parameter names only allowed for request parameters");
                checkState(delimiter == null, "Delimiter not allowed for fixed or path parameters");
            } else {
                checkState(value == null, "Value only allowed for fixed parameters");
                checkState(!optional, "Optional is only allowed for path parameters");
                checkState(suffix == null, "Suffix is only allowed for path parameters");
                checkState(prefix == null, "Prefix is only allowed for path parameters");
                if (hasSingleIncomingParameter()) {
                    checkState(delimiter == null, "Delimiter not allowed for non-aggregation request parameters");
                } else {
                    checkState(delimiter != null, "Delimiter must be present for request parameter aggregation");
                }
            }

            if (!isFixedParam()) {
                checkState(!first, "First is only valid for fixed parameters");
            }
        }

        private boolean isFixedParam() {
            return value != null;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, incomingName, prefix, suffix, delimiter, optional, first);
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this).add("name", name).add("incomingName", incomingName)
                          .add("prefix", prefix).add("suffix", suffix).add("delimiter", delimiter)
                          .add("optional", optional).toString();
        }

        Handler toHandler() {
            if (isSeoParameter()) {
                return PathParamHandlers.addSeoParameter(optional);
            } else if (isVariablePathParam()) {
                return PathParamHandlers.addPathKey(value);
            } else if (isFixedParam()) {
                if (first) {
                    return PostProcessors.addFirstParameterValue(name, value);
                } else {
                    return PostProcessors.addFixedParameterValue(name, value);
                }
            } else {
                if (isPathParam()) {

                    PathParamHandler paramHandler = PathParamHandlers.mapToParameter(name);
                    if (hasPrefix()) {
                        paramHandler = PathParamHandlers.requirePrefix(prefix, paramHandler);
                    }

                    if (hasSuffix()) {
                        paramHandler = PathParamHandlers.requireSuffix(suffix, paramHandler);
                    }

                    return paramHandler;
                } else {
                    RequestParamHandler handler;
                    if (hasSingleIncomingParameter()) {
                        handler = RequestParamHandlers.mapIncomingParameter(incomingName.get(0), name);
                    } else {
                        handler = RequestParamHandlers.aggregate(name, delimiter, incomingName.get(0),
                                incomingName.get(1), Iterables.toArray(skip(incomingName, 2), String.class));
                    }

                    return handler;
                }
            }
        }

        void serialize(final Appendable appendable) throws IOException {
            appendable.append(INDENT).append(PARAM_PREFIX);

            final Map<ParamKey, String> paramTokens = Maps.newEnumMap(ParamKey.class);
            if (!isSeoParameter() && !isVariablePathParam()) {
                paramTokens.put(ParamKey.NAME, name);
            }

            if (isSeoParameter()) {
                paramTokens.put(ParamKey.SEO, TRUE);
                if (optional) {
                    paramTokens.put(ParamKey.OPTIONAL, TRUE);
                }
            } else if (isVariablePathParam()) {
                paramTokens.put(ParamKey.PATHVARIABLE, value);
                if (optional) {
                    paramTokens.put(ParamKey.OPTIONAL, TRUE);
                }
            } else if (isFixedParam()) {
                paramTokens.put(ParamKey.FIXEDVALUE, value);
                if (first) {
                    paramTokens.put(ParamKey.FIRST, TRUE);
                }
            } else if (isPathParam()) { // it's a path parameter
                if (optional) {
                    paramTokens.put(ParamKey.OPTIONAL, TRUE);
                }

                if (hasSuffix()) {
                    paramTokens.put(ParamKey.SUFFIX, suffix);
                }

                if (hasPrefix()) {
                    paramTokens.put(ParamKey.PREFIX, prefix);
                }
            } else {                                // it's a request parameter
                if (hasSingleIncomingParameter()) { // it's a single mapped request parameter
                    paramTokens.put(ParamKey.REQUESTPARAM, incomingName.get(0));
                } else {                            // it's an aggregation
                    paramTokens.put(ParamKey.AGGREGATE,
                        COMMA.joiner().appendTo(new StringBuilder().append(delimiter), incomingName).toString());
                }
            }

            MAP_JOINER.appendTo(appendable, paramTokens).append(NEWLINE);
        }

        private boolean isSeoParameter() {
            return SEO.equals(name);
        }

        private boolean isVariablePathParam() {
            return (value != null) && FROM_INPUT.equals(name);
        }

        private boolean hasSingleIncomingParameter() {
            return incomingName.size() == 1;
        }

        private boolean hasPrefix() {
            return prefix != null;
        }

        private boolean hasSuffix() {
            return suffix != null;
        }

        private boolean isAnyTypeOfPathParam() {
            return isPathParam() || isSeoParameter() || isVariablePathParam();
        }

        private boolean isPathParam() {
            return incomingName.isEmpty() && !isFixedParam();
        }
    }

    public static final String RULE_DELIMITER = repeat(NEWLINE, 2) + repeat("-", 80) + NEWLINE;
    private static final String FROM_INPUT = "**from input**";
    private static final String SEO = "**seo**";

    /**
     * Deserialize a list of rule descriptions from a character stream.
     */
    public static List<RuleSetDescription> deserialize(final InputStream data) {
        try {
            return CharStreams.readLines(new InputSupplier<Reader>() {
                        @Override
                        public Reader getInput() throws IOException {
                            return new InputStreamReader(data);
                        }
                    }, new Deserializer());
        } catch (final IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Serialize this rule description.
     */
    public void serialize(final Appendable appendable) throws IOException {
        appendable.append(RULE_PREFIX).append(id).append(NEWLINE);
        appendable.append(INDENT).append(TARGET_PREFIX).append(getTargetUrl()).append(NEWLINE);
        for (final String path : paths) {
            appendable.append(INDENT).append(PATH_PREFIX).append(path).append(NEWLINE);
        }

        for (final Parameter parameter : parameters) {
            parameter.serialize(appendable);
        }
    }

    /**
     * Serialize an iterable of Rule Descriptions.
     */
    public static void serialize(final Iterable<RuleSetDescription> rules,
            final OutputSupplier<? extends Writer> writer) {
        try {
            final StringBuilder appendable = new StringBuilder();
            String delim = "";
            for (final RuleSetDescription ruleSetDescription : rules) {
                appendable.append(delim);
                ruleSetDescription.serialize(appendable);
                delim = RULE_DELIMITER;
            }

            CharStreams.write(appendable, writer);
        } catch (final Exception e) {
            throw new IllegalStateException("Error serializing Mapping rules", e);
        }
    }

    /**
     * Add a path parameter.
     *
     * @param  name  the name of the outgoing parameter
     */
    public RuleSetDescription addPathParameter(final String name) {
        return addPathParameter(name, null, null, false);
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(final String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Add a fixed key / value mapping.
     */
    public RuleSetDescription addFixedParameter(final String name, final String value) {
        final Parameter param = new Parameter();
        param.name = name;
        param.value = value;
        prepareParams().add(param);
        return this;
    }

    /**
     * Add a parameter mapping where the incoming path segment will be the key and the supplied parameter will be the
     * value.
     */
    public RuleSetDescription addPathKey(final String value) {
        return addPathKey(value, false);
    }

    /**
     * Add a parameter mapping where the incoming path segment will be the key and the supplied parameter will be the
     * value. if the boolean flag is set to true, the path key will be optional
     */
    public RuleSetDescription addPathKey(final String value, final boolean optional) {
        final Parameter parameter = new Parameter();
        parameter.name = FROM_INPUT;
        parameter.optional = optional;
        parameter.value = value;
        prepareParams().add(parameter);
        return this;
    }

    /**
     * Add a path parameter that will be used for rule resolution, but won't change the outgoing mapping.
     *
     * @param  optional  if true, the parameter is optional
     */
    public RuleSetDescription addSeoParameter(final boolean optional) {
        final Parameter parameter = new Parameter();
        parameter.name = SEO;
        parameter.optional = optional;
        prepareParams().add(parameter);
        return this;
    }

    /**
     * Add a fixed key / value pair that will always be used as the first parameter.
     */
    public RuleSetDescription addFirstParameter(final String name, final String value) {
        final Parameter param = new Parameter();
        param.name = name;
        param.value = value;
        param.first = true;
        prepareParams().add(param);
        return this;
    }
}
