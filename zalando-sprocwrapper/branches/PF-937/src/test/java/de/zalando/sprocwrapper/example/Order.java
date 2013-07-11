package de.zalando.sprocwrapper.example;

import de.zalando.sprocwrapper.example.transformer.MoneyObjectMapper;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType
public class Order {

    @DatabaseField
    public String orderNumber;

    @DatabaseField(mapper = MoneyObjectMapper.class)
    public TobisAmount amount;

    public Order(final String on, final TobisAmount a) {
        orderNumber = on;
        amount = a;
    }

    public Order() { }
}
