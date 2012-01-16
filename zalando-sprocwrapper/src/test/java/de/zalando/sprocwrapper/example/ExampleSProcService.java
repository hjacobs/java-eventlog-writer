package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardIdentityStrategy;

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

    @SProcCall
    long getSimpleLong();

    @SProcCall
    int getSimpleInt(@SProcParam int i);

    @SProcCall
    void getSimpleIntVoid(@SProcParam int i);

    @SProcCall(sql = "SELECT 'a' AS a, 'b' AS b UNION ALL SELECT 'c', 'd'")
    List<ExampleDomainObject> getResult();

    @SProcCall(sql = "SELECT 'a' AS a, 'b' AS b")
    ExampleDomainObject getSingleResult();

    @SProcCall(sql = "SELECT 5555")
    Integer getBla();

    @SProcCall(shardStrategy = VirtualShardIdentityStrategy.class)
    int getShardIndex(@ShardKey int shard);

    @SProcCall
    String createOrUpdateObject(@SProcParam ExampleDomainObject object);

    @SProcCall
    String createOrUpdateMultipleObjects(
            @SProcParam(type = "example_domain_object[]") List<ExampleDomainObject> objects);

    @SProcCall
    String createOrUpdateMultipleObjectsWithMap(
            @SProcParam(type = "example_domain_object_with_map[]") List<ExampleDomainObjectWithMap> objects);

    @SProcCall
    String createOrUpdateMultipleObjectsWithInnerObject(
            @SProcParam(type = "example_domain_object_with_inner_object[]") List<ExampleDomainObjectWithInnerObject> objects);

    @SProcCall
    void createOrUpdateMultipleObjectsWithMapVoid(
            @SProcParam(type = "example_domain_object_with_map[]") List<ExampleDomainObjectWithMap> objects);

    @SProcCall
    boolean reserveStock(@ShardKey @SProcParam String sku);
}
