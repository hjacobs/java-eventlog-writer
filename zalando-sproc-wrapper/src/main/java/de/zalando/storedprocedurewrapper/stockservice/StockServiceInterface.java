package de.zalando.storedprocedurewrapper.stockservice;

import java.util.List;

import de.zalando.storedprocedurewrapper.SprocProxyServiceInterface;
import de.zalando.storedprocedurewrapper.VirtualShardIdFromLong;
import de.zalando.storedprocedurewrapper.annotations.ShardKey;
import de.zalando.storedprocedurewrapper.annotations.SprocCall;
import de.zalando.storedprocedurewrapper.annotations.SprocParam;

/**
 * @author  jmussler
 */
public interface StockServiceInterface extends SprocProxyServiceInterface {
    @SprocCall(name = "create_article_simple")
    void createArticleSimple(@SprocParam String sku);

    @SprocCall(name = "create_article_simple_items")
    String createArticleSimpleItems(@SprocParam(name = "sku")
            @ShardKey String sku,
            @SprocParam(name = "stockid") int stockId,
            @SprocParam(name = "quantity") int quantity,
            @SprocParam(name = "price") int purchasePrice,
            @SprocParam(name = "referencenumber") String referenceNumber);

    @SprocCall(name = "getSimpleInt")
    Integer getSimpleInt();

    @SprocCall(sql = "SELECT 100")
    Integer getOtherInt();

    @SprocCall(sql = "SELECT ?")
    Integer getSelectValue(@SprocParam int i);

    @SprocCall(sql = "SELECT 'a' AS a, 'b' AS b UNION ALL SELECT 'c', 'd'")
    List<TestResult> getResult();

    @SprocCall(sql = "SELECT 'a' AS a, 'b' AS b")
    TestResult getSingleResult();

    @SprocCall(sql = "SELECT 5555")
    Integer getBla();

    @SprocCall(sql = "SELECT current_database()", shardStrategy = VirtualShardIdFromLong.class)
    String getDatabase(@ShardKey int shard);
}
