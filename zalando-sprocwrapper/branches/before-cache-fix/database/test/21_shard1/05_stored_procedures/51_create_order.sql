CREATE OR REPLACE FUNCTION ztest_shard1.create_order(order_number text, amount ztest_shard1.monetary_amount) RETURNS INT AS
$$
INSERT INTO ztest_shard1.order ( order_number, amount ) values ( $1 , $2 ) returning id
$$ LANGUAGE 'sql' SECURITY DEFINER;