CREATE TYPE price_change AS (
    sku text,
    partner_id int,
    change_date timestamptz
);

COMMENT ON TYPE price_change IS 'This is a price change event, without the price. Because the price needs to be materialized before it is sure to be a change.';
