package de.zalando.sprocwrapper.example.transformer;

import java.math.BigDecimal;

import java.util.Collections;
import java.util.List;
import java.util.TreeMap;

import de.zalando.sprocwrapper.example.TobisAmount;
import de.zalando.sprocwrapper.example.TobisAmountImpl;
import de.zalando.sprocwrapper.globalobjecttransformer.annotation.GlobalObjectMapper;

import de.zalando.typemapper.core.fieldMapper.ObjectMapper;
import de.zalando.typemapper.core.result.DbResultNode;
import de.zalando.typemapper.postgres.PgTypeHelper.PgTypeDataHolder;

/**
 * @author  danieldelhoyo
 */
@GlobalObjectMapper
public class MoneyObjectMapper extends ObjectMapper<TobisAmount> {
    @Override
    public TobisAmount unmarshalFromDbNode(final DbResultNode dbResultNode) {
        List<DbResultNode> dbResultNodeList = dbResultNode.getChildren();
        BigDecimal amount = new BigDecimal(dbResultNodeList.get(0).getValue());
        String currency = dbResultNodeList.get(1).getValue();

        return new TobisAmountImpl(amount, currency);
    }

    @Override
    public PgTypeDataHolder marshalToDb(final TobisAmount t) {
        TreeMap<Integer, Object> resultPositionMap = new TreeMap<Integer, Object>();
        resultPositionMap.put(1, t.getAmount());
        resultPositionMap.put(2, t.getCurrency());
        return new PgTypeDataHolder("monetary_amount", Collections.unmodifiableCollection(resultPositionMap.values()));
    }

}
