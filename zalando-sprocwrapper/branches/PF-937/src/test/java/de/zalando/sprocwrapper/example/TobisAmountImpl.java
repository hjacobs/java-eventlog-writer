package de.zalando.sprocwrapper.example;

import java.math.BigDecimal;

/**
 * @author  jmussler
 */
public class TobisAmountImpl implements TobisAmount {

    public TobisAmountImpl() { }

    public TobisAmountImpl(final BigDecimal a, final String c) {
        currency = c;
        amount = a;
    }

    public String currency;

    public BigDecimal amount;

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }
}
