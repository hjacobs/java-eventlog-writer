create type accounting_config as (
  version                   integer,
  config_sku                text,
  initial_purchase_price    integer,
  initial_purchase_currency zz_commons.currency,
  last_purchase_price       integer,
  last_purchase_currency    zz_commons.currency,
  valuation_price           integer,
  valuation_currency        zz_commons.currency,
  landed_cost_price         integer,
  landed_cost_currency      zz_commons.currency,
  amortization_rate         integer,
  simple_facets             accounting_simple[]
);
