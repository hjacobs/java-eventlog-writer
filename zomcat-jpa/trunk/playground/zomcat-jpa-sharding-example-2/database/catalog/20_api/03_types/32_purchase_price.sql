CREATE TYPE purchase_price AS (
  simple_sku                    text,
  initial_purchase_price        integer,
  initial_purchase_currency     text,
  last_purchase_price           integer,
  last_purchase_currency        text
);