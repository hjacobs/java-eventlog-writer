package de.zalando.sprocwrapper.example.transformer;

import java.math.BigDecimal;

import java.util.List;

import de.zalando.sprocwrapper.example.MonetaryAmount;
import de.zalando.sprocwrapper.example.TobisAmount;
import de.zalando.sprocwrapper.example.TobisAmountImpl;
import de.zalando.sprocwrapper.globalobjecttransformer.annotation.GlobalObjectMapper;

import de.zalando.typemapper.core.fieldMapper.ObjectMapper;
import de.zalando.typemapper.core.result.DbResultNode;

/**
 * @author  danieldelhoyo
 */
@GlobalObjectMapper
public class MoneyObjectMapper extends ObjectMapper<MonetaryAmount, TobisAmount> {
    @Override
    public TobisAmount unmarshalFromDbNode(final DbResultNode dbResultNode) {
        List<DbResultNode> dbResultNodeList = dbResultNode.getChildren();
        BigDecimal amount = new BigDecimal(dbResultNodeList.get(0).getValue());
        String currency = dbResultNodeList.get(1).getValue();

        return new TobisAmountImpl(amount, currency);
    }

    @Override
    public MonetaryAmount marshalToDb(final TobisAmount t) {
        return new MonetaryAmount(t.getAmount(), t.getCurrency());
    }

}
