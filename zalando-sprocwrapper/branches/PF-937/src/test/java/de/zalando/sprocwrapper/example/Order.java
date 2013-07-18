package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.sprocwrapper.example.transformer.MoneyObjectMapper;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType(name = "order_type")
public class Order {

    @DatabaseField
    public String orderNumber;

    @DatabaseField(mapper = MoneyObjectMapper.class)
    public TobisAmount amount;

    @DatabaseField
    public List<OrderPosition> positions;

    public Order(final String on, final TobisAmount a) {
        orderNumber = on;
        amount = a;
    }

    public Order() { }
}
