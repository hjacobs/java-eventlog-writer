package de.zalando.util.web.urlmapping.domain;

import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

public final class MappingInfo {

    private final String basePath;

    private MappingInfo(final String externalUrl, final String internalPath, final Map<String, String> mappedParameters,
            final String basePath) {
        this.externalUrl = externalUrl;
        this.internalPath = internalPath;
        this.basePath = basePath;
        this.mappedParameters = ImmutableMap.copyOf(mappedParameters);
    }

    /**
     * This is the name of the request attribute that identifies the associated {@link MappingInfo} object.
     */
    public static final String MAPPINGINFO_ATTRIBUTE_NAME = "zalando.urlmapping.info";

    /**
     * Factory method for creating a {@link MappingInfo} object.
     *
     * @param  externalUrl
     * @param  internalPath
     * @param  mappedParameters
     * @param  basePath
     */
    public static MappingInfo create(final String externalUrl, final String internalPath,
            final Map<String, String> mappedParameters, final String basePath) {
        return new MappingInfo(externalUrl, internalPath, mappedParameters, basePath);
    }

    /**
     * Register the MappingInfo as request attribute of the supplied request.
     */
    public void tieToRequest(final ServletRequest request) {
        request.setAttribute(MAPPINGINFO_ATTRIBUTE_NAME, this);
    }

    /**
     * Get the MappingInfo object associated with the current request.
     *
     * @return  null is no MappingInfo is bound to the request (i.e. request wasn't mapped by
     *          {@link de.zalando.util.web.urlmapping.RuleContext}.
     */
    public static MappingInfo forRequest(final HttpServletRequest request) {
        final Object attribute = request.getAttribute(MAPPINGINFO_ATTRIBUTE_NAME);
        if (attribute instanceof MappingInfo) {
            return ((MappingInfo) attribute);
        } else {
            return null;
        }
    }

    /**
     * Gets an {@link Optional} MappingInfo object associated with the current request. {@link Optional#isPresent()}
     * will be true if and only if the request was mapped by the mapping processor.
     *
     * @see  Optional
     */
    public static Optional<MappingInfo> optionalForRequest(final HttpServletRequest request) {
        final Object attribute = request.getAttribute(MAPPINGINFO_ATTRIBUTE_NAME);
        if (attribute instanceof MappingInfo) {
            return Optional.of((MappingInfo) attribute);
        } else {
            return Optional.absent();
        }
    }

    private final String externalUrl;
    private final String internalPath;

    private final Map<String, String> mappedParameters;

    /**
     * Gets the original external URL.
     */
    public String getExternalUrl() {
        return externalUrl;
    }

    /**
     * Gets the internal context-relative URL path.
     */
    public String getInternalPath() {
        return internalPath;
    }

    /**
     * Returns an immutable map containing all parameters that were set by the URL mapping.
     */
    public Map<String, String> getMappedParameters() {
        return mappedParameters;
    }

    /**
     * Returns the static part of the incoming request path (all path segments that were not mapped to parameters).
     */
    public String getBasePath() {
        return basePath;
    }

}
