package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardIdFromLongStrategy;

/**
 * @author  jmussler
 */
public interface ExampleSProcService extends SProcService {
    @SProcCall(name = "create_article_simple")
    void createArticleSimple(@SProcParam String sku);

    @SProcCall(name = "create_article_simple_items")
    String createArticleSimpleItems(@SProcParam(name = "sku")
            @ShardKey String sku, @SProcParam int stockId,
            @SProcParam(name = "quantity") int quantity,
            @SProcParam(name = "price") int purchasePrice,
            @SProcParam(name = "referencenumber") String referenceNumber);

    @SProcCall
    Integer getSimpleInt();

    @SProcCall(name = "get_simple_int")
    int getSimpleIntAsPrimitive();

    @SProcCall(name = "get_simple_int")
    void getSimpleIntIgnore();

    @SProcCall(sql = "SELECT 100")
    Integer getOtherInt();

    @SProcCall(sql = "SELECT ?")
    Integer getSelectValue(@SProcParam int i);

    @SProcCall(sql = "SELECT 'a' AS a, 'b' AS b UNION ALL SELECT 'c', 'd'")
    List<ExampleResult> getResult();

    @SProcCall(sql = "SELECT 'a' AS a, 'b' AS b")
    ExampleResult getSingleResult();

    @SProcCall(sql = "SELECT 5555")
    Integer getBla();

    @SProcCall(sql = "SELECT current_database()", shardStrategy = VirtualShardIdFromLongStrategy.class)
    String getDatabase(@ShardKey int shard);
}
