package de.zalando.sprocwrapper.example;

import java.util.List;

import com.google.common.base.Optional;

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

    @DatabaseField
    public Optional<AddressPojo> address = Optional.absent();

    public Order(final String on, final TobisAmount a) {
        this(on, a, null);
    }

    public Order(final String on, final TobisAmount a, final AddressPojo address) {
        this.orderNumber = on;
        this.amount = a;
        this.address = Optional.fromNullable(address);
    }

    public Order() { }
}
