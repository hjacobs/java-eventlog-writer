package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType
public class Order {

    @DatabaseField
    public String orderNumber;

    @DatabaseField(transformer = de.zalando.sprocwrapper.example.transformer.TobisAmountTransformer.class)
    public TobisAmount amount;

    public Order(final String on, final TobisAmount a) {
        orderNumber = on;
        amount = a;
    }

    public Order() { }
}
