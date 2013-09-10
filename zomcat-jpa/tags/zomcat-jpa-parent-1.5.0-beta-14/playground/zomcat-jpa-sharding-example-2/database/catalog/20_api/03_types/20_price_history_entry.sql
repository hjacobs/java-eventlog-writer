CREATE TYPE price_history_entry AS (
    simple_sku              text,
    start_date              timestamptz,
    end_date                timestamptz,
    app_domain_id           smallint,
    price                   integer,
    promotional_price       integer
);