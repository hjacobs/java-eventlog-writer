package de.zalando.sprocwrapper.example;

import java.math.BigDecimal;

public interface TobisAmount {
    String getCurrency();

    void setCurrency(final String currency);

    BigDecimal getAmount();

    void setAmount(final BigDecimal amount);
}
