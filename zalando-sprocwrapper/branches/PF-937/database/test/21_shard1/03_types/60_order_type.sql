CREATE TYPE order_position_type AS (
    amount          ztest_shard1.monetary_amount
);

CREATE TYPE order_type AS (
    order_number    text,
    amount          ztest_shard1.monetary_amount,
    positions       ztest_shard1.order_position_type[]
);