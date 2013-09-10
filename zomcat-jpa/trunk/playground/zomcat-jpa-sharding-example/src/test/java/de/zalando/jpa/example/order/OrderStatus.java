package de.zalando.jpa.example.order;

/**
 * State of an {@link de.zalando.jpa.example.order.ArticleSku}. Just for testing with SessionCustomizer.
 *
 * @author  jbellmann
 */
public enum OrderStatus {

    INITIAL,
    ORDERED,
    CANCELED;

}
