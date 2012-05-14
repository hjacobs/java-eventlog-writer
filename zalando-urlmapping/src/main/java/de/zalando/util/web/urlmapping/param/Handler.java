package de.zalando.util.web.urlmapping.param;

import java.io.Serializable;

import de.zalando.util.web.urlmapping.builder.UrlBuilder;

/**
 * Marker interface to group different kinds of handlers that work on {@link UrlBuilder} objects. All Handler
 * implementations must be immutable and stateless.
 */
public interface Handler extends Serializable { }
