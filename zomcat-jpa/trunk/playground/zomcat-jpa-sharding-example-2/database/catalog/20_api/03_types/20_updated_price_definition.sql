CREATE TYPE updated_price_definition AS (
    price_definition    price_definition,
    operation           text,
    is_high_priority    boolean
);
