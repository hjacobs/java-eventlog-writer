/**
 *
 */
package de.zalando.util.web.urlmapping.rule;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.repeat;
import static com.google.common.collect.Iterables.skip;
import static com.google.common.collect.Lists.newArrayList;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.io.OutputSupplier;

import de.zalando.util.web.urlmapping.RuleContext;
import de.zalando.util.web.urlmapping.builder.PathBuilder;
import de.zalando.util.web.urlmapping.param.Handler;
import de.zalando.util.web.urlmapping.param.PathParamHandler;
import de.zalando.util.web.urlmapping.param.PathParamHandlers;
import de.zalando.util.web.urlmapping.param.PostProcessors;
import de.zalando.util.web.urlmapping.param.RequestParamHandler;
import de.zalando.util.web.urlmapping.param.RequestParamHandlers;
import de.zalando.util.web.urlmapping.param.UrlPostProcessors;
import de.zalando.util.web.urlmapping.util.Delimiter;

/**
 * This class handles serialization and deserialization of rulesets.
 *
 * @author  Sean Patrick Floyd (sean.floyd@zalando.de)
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
public class RuleSetDescription {

    static final String NEWLINE = "\n";
    static final String INDENT = "  ";

    private String id;

    private Integer priority;

    private TreeSet<String> paths = Sets.newTreeSet();

    @JsonProperty
    private List<Parameter> parameters = ImmutableList.of();

    @JsonProperty
    private List<RuleMappingTarget> ruleMappingTargets = newArrayList();

    private static final Ordering<Parameter> PARAMETER_ORDERING = new Ordering<Parameter>() {
        //J-
        @Override
        public int compare(final Parameter left, final Parameter right) {
            return com.google.common.collect.ComparisonChain.start()
                    .compareTrueFirst(right.isAnyTypeOfPathParam(), left.isAnyTypeOfPathParam())
                    .compareFalseFirst(left.optional, right.optional)
                    .result();
        }
        //J+
    };

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof RuleSetDescription) {
            final RuleSetDescription other = (RuleSetDescription) obj;
            return Objects.equal(id, other.id) && Objects.equal(paths, other.paths)
                    && Objects.equal(parameters, other.parameters)
                    && Objects.equal(ruleMappingTargets, other.ruleMappingTargets);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, paths, parameters, ruleMappingTargets);
    }

    @Override
    public String toString() {

        return Objects.toStringHelper(this).add("id", id).add("paths", paths)
                      .add("ruleMappingTargets", ruleMappingTargets).toString();
    }

    RuleSetDescription() { }

    public RuleSetDescription(final String id) {
        this.id = id;
    }

    public void checkIntegrity() {
        checkState(id != null, "No Id defined in rule");
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

        for (final RuleMappingTarget variant : ruleMappingTargets) {
            variant.checkIntegrity(id);
        }
    }

    private static int calculatePathLength(final String path) {
        return ImmutableList.copyOf(splitPath(path)).size();
    }

    private static Iterable<String> splitPath(final String path) {
        return Delimiter.SLASH.trimmedSplitter().split(path);
    }

    /**
     * Add a base path to the rule.
     */
    public RuleSetDescription addPath(final String path) {
        paths.add(path);
        return this;
    }

    /**
     * Register this Rule Description with a RuleContext Builder.
     */
    public void register(final RuleContext.Builder contextBuilder) {
        checkIntegrity();

        final int length = calculatePathLength(Iterables.get(paths, 0));

        Iterable<Parameter> parameters = sortParams();

        final List<Handler> allHandlers = ImmutableList.copyOf(Iterables.transform(parameters,
                    Parameter.HANDLER_FUNCTION));
        final MappingRule rule = new ForwardMappingRule(id, priority, length, ruleMappingTargets, allHandlers);

        for (final String basePath : paths) {
            final PathBuilder builder = new PathBuilder();
            for (final String pathItem : splitPath(basePath)) {
                builder.add(pathItem);
            }

            for (final Parameter parameter : parameters) {
                if (parameter.isPathParam() || parameter.isVariablePathParam() || parameter.isSeoParameter()) {
                    if (parameter.isOptional()) {
                        builder.addOptionalWildCard();
                    } else {
                        builder.addWildCard();
                    }
                }
            }

            contextBuilder.addRule(builder.build(), rule);
        }
    }

    public static final String RULE_DELIMITER = repeat(NEWLINE, 2) + repeat("-", 80) + NEWLINE;

    /**
     * Deserialize a list of rule descriptions from a character stream.
     */
    public static List<RuleSetDescription> deserialize(final InputStream data) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        try {

            return mapper.readValue(new InputStreamReader(data), new TypeReference<List<RuleSetDescription>>() { });
        } catch (IOException e) {
            throw new IllegalArgumentException("Got an expection during serlization of rules.", e);
        }
    }

    /**
     * Serialize this rule description.
     */
    public void serialize(final OutputSupplier<? extends Writer> writer) throws IOException {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationConfig.Feature.INDENT_OUTPUT);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.writerWithDefaultPrettyPrinter().writeValue(writer.getOutput(), this);

    }

    /**
     * Serialize an iterable of Rule Descriptions.
     */
    public static void serialize(final Iterable<RuleSetDescription> rules,
            final OutputSupplier<? extends Writer> writer) {
        SerializationConfig config;
        ObjectMapper mapper = new ObjectMapper().enable(SerializationConfig.Feature.INDENT_OUTPUT);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(writer.getOutput(), ImmutableList.copyOf(rules));
        } catch (IOException e) {
            throw new IllegalArgumentException("Got an expection during serlization of rules.", e);
        }
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setPriority(final Integer priority) {
        this.priority = priority;
    }

    public Integer getPriority() {
        return priority;
    }

    @VisibleForTesting
    @Nonnull
    public Set<String> getPaths() {
        return paths;
    }

    private static final ImmutableSet<String> TargetTypeStringValues = ImmutableSet.copyOf(Iterables.transform(
                ImmutableList.copyOf(ForwardMappingRule.TargetType.values()),
                new Function<ForwardMappingRule.TargetType, String>() {
                    @Nullable
                    public String apply(@Nullable final ForwardMappingRule.TargetType input) {
                        return input.name();
                    }
                }));

    public void addRuleMappingTarget(final RuleMappingTarget target) {
        checkArgument(target != null, "Parameter target must not be null.");
        ruleMappingTargets.add(target);
    }

    public List<Parameter> sortParams() {

        // path params before non-path params
        Collections.sort(prepareParams(), PARAMETER_ORDERING);

        return parameters;
    }

    List<Parameter> prepareParams() {
        if (parameters.isEmpty()) {
            parameters = newArrayList();
        }

        return parameters;
    }

    public List<Parameter> getParameters() {
        return prepareParams();
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

    public RuleSetDescription addPathSegmentParameter(final String variableName, final int originalOffset) {
        final Parameter param = new Parameter();
        param.name = variableName;
        param.segment = originalOffset;
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
     * Add a parameter mapping where the incoming path segment will be the key and the supplied parameter will be the
     * value.
     */
    public RuleSetDescription addPathKey(final String value) {
        return addPathKey(value, false);
    }

    /**
     * Add a path parameter.
     *
     * @param  name  the name of the outgoing parameter
     */
    public RuleSetDescription addPathParameter(final String name) {
        return addPathParameter(name, null, null, false);
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
        PATHSEGMENT,
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

    @JsonSerialize(include = JsonSerialize.Inclusion.NON_DEFAULT)
    static class Parameter {
        private static final String TRUE = Boolean.TRUE.toString();

        public static final Function<Parameter, Handler> HANDLER_FUNCTION = new Function<Parameter, Handler>() {

            @Override
            public Handler apply(final Parameter input) {
                return input.toHandler();
            }
        };

        @JsonProperty
        private String name;
        @JsonProperty
        @JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
        private List<String> incomingName = emptyList();
        @JsonProperty
        private Character delimiter;
        @JsonProperty
        private String suffix;
        @JsonProperty
        private String prefix;
        @JsonProperty
        private boolean first;
        @JsonProperty
        private boolean optional;
        @JsonProperty
        private int segment = -1;
        @JsonProperty
        public String value;

        @Override
        public boolean equals(final Object obj) {
            if (obj instanceof Parameter) {
                final Parameter other = (Parameter) obj;
                return Objects.equal(name, other.name) && Objects.equal(incomingName, other.incomingName)
                        && Objects.equal(prefix, other.prefix) && Objects.equal(suffix, other.suffix)
                        && Objects.equal(delimiter, other.delimiter) && Objects.equal(optional, other.optional)
                        && Objects.equal(first, other.first);
            } else {
                return false;
            }
        }

        boolean isOptional() {
            return optional;
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
            } else if (isSegment()) {
                return UrlPostProcessors.pathInterpolator(name, segment);
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

        private boolean isSegment() {
            return segment >= 0 && name != null;
        }

        boolean isSeoParameter() {
            return SEO.equals(name);
        }

        boolean isVariablePathParam() {
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

        boolean isAnyTypeOfPathParam() {
            return isPathParam() || isSeoParameter() || isVariablePathParam();
        }

        boolean isPathParam() {
            return incomingName.isEmpty() && !isFixedParam();
        }
    }

    private static final String FROM_INPUT = "**from input**";
    private static final String SEO = "**seo**";
    private static final Joiner.MapJoiner MAP_JOINER = Joiner.on(';').withKeyValueSeparator("=");
}
