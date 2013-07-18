CREATE OR REPLACE FUNCTION ztest_shard1.create_order(order_number text, amount ztest_shard1.monetary_amount) RETURNS INT AS
$$
    INSERT INTO ztest_shard1."order" ( order_number, amount ) VALUES ( $1 , $2 ) RETURNING id;
$$ LANGUAGE 'sql' SECURITY DEFINER;

CREATE OR REPLACE FUNCTION ztest_shard1.create_order(p_order ztest_shard1.order_type) RETURNS INT AS
$$
DECLARE
    l_order_id int;
BEGIN
    SELECT ztest_shard1.create_order($1.order_number, $1.amount) INTO l_order_id;

    INSERT INTO ztest_shard1.order_position ( order_id, amount ) (SELECT l_order_id, pos.amount FROM unnest($1.positions) AS pos);

    RETURN l_order_id;
END;
$$ LANGUAGE 'plpgsql' SECURITY DEFINER;