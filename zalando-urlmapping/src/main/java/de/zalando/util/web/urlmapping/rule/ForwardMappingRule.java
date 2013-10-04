package de.zalando.util.web.urlmapping.rule;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.limit;
import static com.google.common.collect.Iterables.skip;
import static com.google.common.collect.Maps.newLinkedHashMap;

import static de.zalando.util.web.urlmapping.util.Helper.getOriginalUrl;

import java.io.IOException;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;

import de.zalando.util.web.urlmapping.MappingContext;
import de.zalando.util.web.urlmapping.UrlMappingException;
import de.zalando.util.web.urlmapping.builder.UrlBuilder;
import de.zalando.util.web.urlmapping.domain.MappingInfo;
import de.zalando.util.web.urlmapping.param.Handler;
import de.zalando.util.web.urlmapping.param.PathParamHandler;
import de.zalando.util.web.urlmapping.param.PostProcessor;
import de.zalando.util.web.urlmapping.param.RequestParamHandler;
import de.zalando.util.web.urlmapping.param.UrlPostProcessor;
import de.zalando.util.web.urlmapping.util.Delimiter;

public class ForwardMappingRule implements MappingRule {

    private static final ImmutableList<String> EMPTY_STRING_LIST = ImmutableList.of("");
    private static final long serialVersionUID = -6311688030427393665L;
    private final List<PathParamHandler> paramHandlers;
    private final List<PostProcessor> postProcessors;
    private final List<RequestParamHandler> requestParamHandlers;
    private final List<UrlPostProcessor> urlPostProcessors;
    private final int cardinality;
    private final int handlersCount;
    private final String id;
    private final List<RuleMappingTarget> ruleMappingTargets;
    @Nullable
    private Integer priority;

    public enum TargetType {
        STATIC,
        SPRING,
        STRIPES
    }

    public ForwardMappingRule(@Nonnull final String id, @Nullable final Integer priority, final int cardinality,
            @Nonnull final List<RuleMappingTarget> ruleMappingTargets, @Nonnull final List<Handler> handlers) {
        this.priority = priority;
        this.id = checkNotNull(id, "An id required for all rules");
        this.cardinality = cardinality;
        this.ruleMappingTargets = checkNotNull(ruleMappingTargets,
                "At least one rule mapping target has to be defined");
        checkArgument(!this.ruleMappingTargets.isEmpty(), "At least one rule mapping target has to be defined.");

        final Builder<RequestParamHandler> requestParamsBuilder = ImmutableList.builder();
        final Builder<PathParamHandler> pathParamsBuilder = ImmutableList.builder();
        final Builder<PostProcessor> postProcessorBuilder = ImmutableList.builder();
        final Builder<UrlPostProcessor> urlPostProcessorBuilder = ImmutableList.builder();
        for (final Handler handler : Objects.firstNonNull(handlers, ImmutableSet.<Handler>of())) {
            if (handler instanceof RequestParamHandler) {
                requestParamsBuilder.add((RequestParamHandler) handler);
            } else if (handler instanceof PathParamHandler) {
                pathParamsBuilder.add((PathParamHandler) handler);
            } else if (handler instanceof PostProcessor) {
                postProcessorBuilder.add((PostProcessor) handler);
            } else if (handler instanceof UrlPostProcessor) {
                urlPostProcessorBuilder.add((UrlPostProcessor) handler);
            }
        }

        requestParamHandlers = requestParamsBuilder.build();
        paramHandlers = pathParamsBuilder.build();
        postProcessors = postProcessorBuilder.build();
        urlPostProcessors = urlPostProcessorBuilder.build();
        handlersCount = requestParamHandlers.size() + paramHandlers.size() + postProcessors.size()
                + urlPostProcessors.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean appliesTo(final MappingContext mappingContext) {

        if (!paramHandlers.isEmpty()) {
            Iterable<String> pathItems = mappingContext.getOriginalPathItems();
            if (cardinality > 0) {
                pathItems = skip(pathItems, cardinality);
            }

            final Iterator<String> segmentIterator = pathItems.iterator();
            final Iterator<PathParamHandler> handlerIterator = paramHandlers.iterator();
            while (segmentIterator.hasNext() && handlerIterator.hasNext()) {
                if (!handlerIterator.next().appliesTo(segmentIterator.next())) {
                    return false;
                }
            }
        }

        if (!requestParamHandlers.isEmpty()) {
            final ListMultimap<String, String> parameterMap = mappingContext.getParameterMap();
            for (final RequestParamHandler handler : requestParamHandlers) {
                if (!handler.appliesTo(parameterMap)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public final void apply(final MappingContext context) throws UrlMappingException {

        String targetUrl = context.applyRuleTargetSwitch(this);

        final UrlBuilder urlBuilder = new UrlBuilder(targetUrl);
        final Map<String, String> mappedParameters = newLinkedHashMap();
        final String basePath;
        final Iterable<String> originalPathItems = context.getOriginalPathItems();
        if (cardinality > 0) {
            basePath = Delimiter.SLASH.joiner().join(concat(EMPTY_STRING_LIST, limit(originalPathItems, cardinality)));
        } else {
            basePath = "/";
        }

        if (!paramHandlers.isEmpty() && (context.getNumberOfSegments() > cardinality)) {
            final Iterator<String> variablePathItems = Iterables.skip(originalPathItems, cardinality).iterator();
            final Iterator<PathParamHandler> pathHandlers = paramHandlers.iterator();
            while (variablePathItems.hasNext() && pathHandlers.hasNext()) {
                final String segment = variablePathItems.next();
                pathHandlers.next().apply(segment, urlBuilder, mappedParameters);
            }

        }

        if (!requestParamHandlers.isEmpty()) {
            final ListMultimap<String, String> parameterMap = context.getParameterMap();
            for (final RequestParamHandler requestParamHandler : requestParamHandlers) {
                requestParamHandler.apply(parameterMap, urlBuilder, mappedParameters);
            }
        }

        for (final PostProcessor postProcessor : postProcessors) {
            postProcessor.postProcess(urlBuilder, context, mappedParameters);
        }

        // Note: Deactivated because of 'forward' behavior:
        // Note: the original parameters will be passed to this forward (because they are part of the 'request'
        // object. New parameters are added by encoding in the 'internalUrl'
        // !! Do not call!! "urlBuilder.takeRemainingParametersFromOriginalMapping(context.getParameterMap());"

        String internalUrl = urlBuilder.build();

        for (UrlPostProcessor urlPostProcessor : urlPostProcessors) {
            internalUrl = urlPostProcessor.postProcess(internalUrl, context, mappedParameters);
        }

        try {
            final HttpServletRequest request = context.getRequest();
            MappingInfo.create(getOriginalUrl(request), internalUrl, mappedParameters, basePath).tieToRequest(request);
            request.getRequestDispatcher(internalUrl).forward(request, context.getResponse());
        } catch (final ServletException e) {
            throw new UrlMappingException("Error forwarding to " + internalUrl, e, context, this);
        } catch (final IOException e) {
            throw new UrlMappingException("Error forwarding to " + internalUrl, e, context, this);
        }

    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof ForwardMappingRule) {
            final ForwardMappingRule other = (ForwardMappingRule) obj;
            return Objects.equal(ruleMappingTargets, other.ruleMappingTargets)
                    && Objects.equal(paramHandlers, other.paramHandlers);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ruleMappingTargets, paramHandlers);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("ruleMappingTargets", ruleMappingTargets)
                      .add("paramHandlers", paramHandlers).toString();
    }

    public int countHandlers() {
        return handlersCount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Integer getPriority() {
        return priority;
    }

    public List<RuleMappingTarget> getRuleMappingTargets() {
        return ImmutableList.copyOf(ruleMappingTargets);
    }
}
