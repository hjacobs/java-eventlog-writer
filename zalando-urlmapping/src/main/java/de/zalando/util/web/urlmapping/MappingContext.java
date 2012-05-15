package de.zalando.util.web.urlmapping;

import static java.util.Collections.emptySet;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Map;
import java.util.Queue;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import de.zalando.util.web.urlmapping.util.Delimiter;

public class MappingContext {

    // we need to keep empty segments in the middle, so no omitEmptyStrings()
    private static final Splitter SLASH_SPLITTER = Splitter.on(Delimiter.SLASH.matcher()).trimResults();

    /**
     * Factory method to create a MappingContext.
     */
    public static MappingContext create(final HttpServletRequest request, final HttpServletResponse response) {
        return new MappingContext(request, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("path", Delimiter.SLASH.joiner().join(originalPathItems))
                      .add("depth", numberOfSegments).add("requestUri", request.getRequestURI()).toString();
    }

    private final int numberOfSegments;
    private final Iterable<String> originalPathItems;
    private final Queue<String> pathItems;
    private final HttpServletRequest request;
    private final HttpServletResponse response;
    private final String trimmedPath;

    private MappingContext(final HttpServletRequest request, final HttpServletResponse response) {
        this.request = request;
        this.response = response;

        // cut off all slashes at both ends, preserve all slashes between segments (including multiple ones)
        // by doing this, we lose empty trailing segments, which is why we must store rules with optional trailing
        // variables in multiple buckets
        trimmedPath = Delimiter.SLASH.matcher().trimFrom(pathFor(request));
        if (trimmedPath.isEmpty()) {
            originalPathItems = emptySet();
            pathItems = NoOpQueue.get();
        } else {
            originalPathItems = SLASH_SPLITTER.split(trimmedPath);
            this.pathItems = Lists.newLinkedList(getOriginalPathItems());
        }

        this.numberOfSegments = pathItems.size();
    }

    private static String pathFor(final HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (requestUri == null) {
            requestUri = "";
        }

        final String contextPath = request.getContextPath();
        String path;
        if (requestUri.startsWith(contextPath)) {

            // Normal case: URI contains context path.
            path = requestUri.substring(contextPath.length());
        } else {
            path = requestUri;
        }

        if ((path == null) || path.equals("")) {
            return "/";
        }

        final int queryOffset = path.indexOf('?');
        if (queryOffset > -1) {
            path = path.substring(0, queryOffset);
        }

        return path;
    }

    /**
     * Remove the current segment from the context.
     */
    public void consumePathSegment() {
        pathItems.poll();
    }

    /**
     * Get the number of path segments the current request has (this will not change while segments are processed).
     */
    public int getNumberOfSegments() {
        return numberOfSegments;
    }

    /**
     * Get the original Servlet Request object.
     */
    public HttpServletRequest getRequest() {
        return request;
    }

    /**
     * Check whether there are any more unprocessed path segments.
     */
    public boolean hasMorePathSegments() {
        return !pathItems.isEmpty();
    }

    /**
     * Get the next un-consumed path segment. You must call {@link #hasMorePathSegments()} in advance to check whether
     * another segment exists. This method will repeatedly return the same element until {@link #consumePathSegment()}
     * is called.
     *
     * @exception  IllegalArgumentException  if there is no such segment
     */
    public String nextPathSegment() {
        checkArgument(!pathItems.isEmpty(),
            "No more path segments available. You forgot to check hasMorePathSegments() first");
        return pathItems.peek();
    }

    /**
     * A generics-safe wrapper around the request parameter map. This map is usually immutable!
     *
     * @see  HttpServletRequest#getParameterMap()
     */
    @SuppressWarnings("unchecked") // the generic type of this map is documented in the servlet api
    public Map<String, String[]> getParameterMap() {
        return request.getParameterMap();
    }

    public Iterable<String> getOriginalPathItems() {
        return originalPathItems;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public String getTrimmedPath() {
        return trimmedPath;
    }

}
