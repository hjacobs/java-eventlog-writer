create type accounting_simple as (
  version                   integer,
  simple_sku                text,
  initial_purchase_price    integer,
  initial_purchase_currency zz_commons.currency,
  last_purchase_price       integer,
  last_purchase_currency    zz_commons.currency,
  valuation_price           integer,
  valuation_currency        zz_commons.currency,
  landed_cost_price         integer,
  landed_cost_currency      zz_commons.currency,
  amortization_rate         integer
);
