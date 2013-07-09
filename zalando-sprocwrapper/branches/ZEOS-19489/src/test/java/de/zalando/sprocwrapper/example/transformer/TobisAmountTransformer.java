
package de.zalando.sprocwrapper.example.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.zalando.sprocwrapper.example.MonetaryAmount;
import de.zalando.sprocwrapper.example.TobisAmount;
import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;

import de.zalando.typemapper.core.ValueTransformer;

/**
 * @author  jmussler
 */
@GlobalValueTransformer
public class TobisAmountTransformer extends ValueTransformer<MonetaryAmount, TobisAmount> {

    private static final Logger LOG = LoggerFactory.getLogger(TobisAmountTransformer.class);

    @Override
    public TobisAmount unmarshalFromDb(final String value) {
        LOG.info(value);
        return new TobisAmount();
    }

    @Override
    public MonetaryAmount marshalToDb(final TobisAmount bound) {
        LOG.info(bound.currency + " " + bound.amount);
        return new MonetaryAmount(bound.amount, bound.currency);
    }

}
