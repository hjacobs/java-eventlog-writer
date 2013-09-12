package de.zalando.sprocwrapper.example;

import java.math.BigDecimal;

/**
 * @author  jmussler
 */
public class TobisAmount {

    public TobisAmount() { }

    public TobisAmount(final BigDecimal a, final String c) {
        currency = c;
        amount = a;
    }

    public String currency;

    public BigDecimal amount;

}
