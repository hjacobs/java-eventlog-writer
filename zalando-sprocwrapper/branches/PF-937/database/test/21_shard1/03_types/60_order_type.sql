CREATE TYPE order_position_type AS (
    amount          ztest_shard1.monetary_amount,
    optional_amount ztest_shard1.monetary_amount,
    address         ztest_shard1.address_type
);

CREATE TYPE order_type AS (
    order_number    text,
    amount          ztest_shard1.monetary_amount,
    address         ztest_shard1.address_type,
    positions       ztest_shard1.order_position_type[]
);