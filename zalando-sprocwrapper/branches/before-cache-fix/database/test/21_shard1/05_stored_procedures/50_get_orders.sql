CREATE OR REPLACE FUNCTION get_orders(IN id int, OUT id int, OUT order_number text, OUT amount ztest_shard1.monetary_amount) RETURNS SETOF record AS
$$
SELECT *
  FROM ztest_shard1.order
 WHERE id = $1
 ORDER BY id ASC
$$ LANGUAGE 'sql' SECURITY DEFINER;