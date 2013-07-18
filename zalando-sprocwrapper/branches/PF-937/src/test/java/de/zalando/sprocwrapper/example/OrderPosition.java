package de.zalando.sprocwrapper.example;

import de.zalando.sprocwrapper.example.transformer.MoneyObjectMapper;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

@DatabaseType(name = "order_position_type")
public class OrderPosition {
    @DatabaseField(mapper = MoneyObjectMapper.class)
    public TobisAmount amount;

    public OrderPosition() { }

    public OrderPosition(final TobisAmount amount) {
        this.amount = amount;
    }

}
