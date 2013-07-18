CREATE TABLE ztest_shard1."order" ( id serial primary key, order_number text, amount ztest_shard1.monetary_amount );

CREATE TABLE ztest_shard1.order_position ( id serial primary key, order_id int references ztest_shard1."order" (id), amount ztest_shard1.monetary_amount );

INSERT INTO ztest_shard1."order" ( order_number, amount ) SELECT 'order1', (1234.567, 'EUR')::ztest_shard1.monetary_amount;
