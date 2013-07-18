CREATE OR REPLACE FUNCTION get_orders(IN id int) RETURNS SETOF ztest_shard1.order_type AS
$$
SELECT ROW(o.order_number,
           o.amount,
           array_agg(ROW(p.amount)::ztest_shard1.order_position_type)
       )::ztest_shard1.order_type
  FROM ztest_shard1."order" o
  LEFT JOIN ztest_shard1.order_position p ON p.order_id=o.id
 WHERE o.id = $1
 GROUP BY o.order_number, o.amount
 ORDER BY o.order_number
$$ LANGUAGE 'sql' SECURITY DEFINER;