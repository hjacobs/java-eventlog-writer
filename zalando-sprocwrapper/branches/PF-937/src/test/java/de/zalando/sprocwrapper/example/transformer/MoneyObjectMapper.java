package de.zalando.sprocwrapper.example.transformer;

import java.math.BigDecimal;

import java.util.List;

import de.zalando.sprocwrapper.example.TobisAmount;

import de.zalando.typemapper.core.fieldMapper.GlobalObjectMapper;
import de.zalando.typemapper.core.result.DbResultNode;

/**
 * @author  danieldelhoyo
 */
public class MoneyObjectMapper implements GlobalObjectMapper {
    @Override
    public Object unmarshalFromDbNode(final DbResultNode dbResultNode) {
        List<DbResultNode> dbResultNodeList = dbResultNode.getChildren();
        BigDecimal amount = new BigDecimal(dbResultNodeList.get(0).getValue());
        String currency = dbResultNodeList.get(1).getValue();

        return new TobisAmount(amount, currency);
    }
}
