package de.zalando.sprocwrapper.example;

import java.util.Date;
import java.util.List;

import de.zalando.sprocwrapper.SProcCall;
import de.zalando.sprocwrapper.SProcCall.VALIDATE;
import de.zalando.sprocwrapper.SProcParam;
import de.zalando.sprocwrapper.SProcService;
import de.zalando.sprocwrapper.sharding.ShardKey;
import de.zalando.sprocwrapper.sharding.VirtualShardIdentityStrategy;

/**
 * @author  jmussler
 */
@SProcService
public interface ExampleSProcService {
    @SProcCall(name = "create_article_simple")
    void createArticleSimple(@SProcParam String sku);

    @SProcCall
    void createArticleSimples(@SProcParam List<String> skus);

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
    boolean getBoolean();

    @SProcCall
    void setBoolean(@SProcParam boolean bool);

    @SProcCall
    void useEnumParam(@SProcParam ExampleEnum enumParameter);

    @SProcCall
    void useDateParam(@SProcParam Date d);

    @SProcCall
    void useDateParam2(@SProcParam(sqlType = java.sql.Types.DATE) Date d);

    @SProcCall
    void useCharParam(@SProcParam char c);

    @SProcCall
    void useIntegerListParam(@SProcParam List<Integer> l);

    @SProcCall
    void getSimpleIntVoid(@SProcParam int i);

    @SProcCall
    boolean login(@SProcParam String userName,
            @SProcParam(sensitive = true) String password);

    @SProcCall(sql = "SELECT 'a' AS a, 'b' AS b UNION ALL SELECT 'c', 'd'")
    List<ExampleDomainObject> getResult();

    @SProcCall(sql = "SELECT 'a' AS a, 'b' AS b")
    ExampleDomainObject getSingleResult();

    @SProcCall(sql = "SELECT 5555")
    Integer getBla();

    @SProcCall(sql = "SELECT '2012-02-03 12:00:21'::timestamp")
    Date getFixedTestDate();

    @SProcCall(shardStrategy = VirtualShardIdentityStrategy.class)
    int getShardIndex(@ShardKey int shard);

    @SProcCall(runOnAllShards = true)
    List<String> collectDataFromAllShards(@SProcParam String someParameter);

    @SProcCall(runOnAllShards = true, parallel = true, name = "collect_data_from_all_shards")
    List<String> collectDataFromAllShardsParallel(@SProcParam String someParameter);

    @SProcCall(sql = "SELECT 1 UNION ALL SELECT 2")
    List<Integer> getInts();

    @SProcCall(sql = "SELECT 1000 UNION ALL SELECT 2002")
    List<Long> getLongs();

    @SProcCall
    String createOrUpdateObject(@SProcParam ExampleDomainObject object);

    @SProcCall
    String createOrUpdateObjectWithRandomFields(@SProcParam ExampleDomainObjectWithRandomFields object);

    @SProcCall
    String createOrUpdateObjectWithEnum(@SProcParam ExampleDomainObjectWithEnum object);

    @SProcCall
    String createOrUpdateObjectWithDate(@SProcParam ExampleDomainObjectWithDate object);

    @SProcCall
    String createOrUpdateMultipleObjects(
            @SProcParam(type = "example_domain_object[]") List<ExampleDomainObject> objects);

    @SProcCall
    String createOrUpdateMultipleObjectsWithRandomFields(
            @SProcParam(type = "example_domain_object_with_random_fields[]") List<ExampleDomainObjectWithRandomFields> object);

    @SProcCall(name = "create_or_update_multiple_objects_with_random_fields")
    String createOrUpdateMultipleObjectsWithRandomFieldsNoAnnotation(
            @SProcParam List<ExampleDomainObjectWithRandomFields> object);

    @SProcCall(name = "create_or_update_multiple_objects_with_random_fields")
    String createOrUpdateMultipleObjectsWithRandomFieldsNoAnnotationOverride(
            @SProcParam List<ExampleDomainObjectWithRandomFieldsOverride> object);

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

    @SProcCall(name = "create_or_update_address")
    AddressPojo createAddress(@SProcParam AddressPojo a);

    @SProcCall(name = "get_address")
    AddressPojo getAddress(@SProcParam AddressPojo a);

    @SProcCall(name = "get_address_sql")
    AddressPojo getAddressSql(@SProcParam AddressPojo a);

    @SProcCall(sql = "SELECT pg_sleep( ? )", timeoutInMilliSeconds = 3 * 1000)
    void testTimeoutSetTo3s(@SProcParam int sleep);

    @SProcCall(sql = "SELECT pg_sleep( ? )", timeoutInMilliSeconds = 5 * 1000)
    void testTimeoutSetTo5s(@SProcParam int sleep);

    @SProcCall(sql = "SHOW statement_timeout")
    String showTimeout();

    @SProcCall(sql = "SELECT 'a','b',null")
    ExampleDomainObjectWithInnerObject getObjectWithNull();

    @SProcCall
    ExampleDomainObjectWithSimpleTransformer testSimpleTransformer(
            @SProcParam ExampleDomainObjectWithSimpleTransformer exampleDomainObjectWithSimpleTransformer);

    @SProcCall
    ExampleDomainObjectWithEnum getEntityWithEnum(@SProcParam long id);

    @SProcCall
    ExampleDomainObjectWithGlobalTransformer testGlobalTransformer(
            @SProcParam ExampleDomainObjectWithGlobalTransformer exampleDomainObjectWithGlobalTransformer);

    @SProcCall(validate = VALIDATE.AS_DEFINED_IN_SERVICE)
    ExampleDomainObjectWithValidation testSprocCallWithoutValidation1(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);

    @SProcCall(validate = VALIDATE.NO)
    ExampleDomainObjectWithValidation testSprocCallWithoutValidation2(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);

    @SProcCall(validate = VALIDATE.YES)
    ExampleDomainObjectWithValidation testSprocCallWithValidation(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);

    @SProcCall(validate = VALIDATE.NO)
    ExampleDomainObjectWithValidation testSprocCallWithValidationInvalidRet1(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);

    @SProcCall(validate = VALIDATE.YES)
    ExampleDomainObjectWithValidation testSprocCallWithValidationInvalidRet2(
            @SProcParam ExampleDomainObjectWithValidation exampleDomainObjectWithValidation);
}
