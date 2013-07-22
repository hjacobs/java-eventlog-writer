
CREATE TABLE zcat_data.article_facet_accounting_simple(
  afas_simple_sku_id              integer              NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_simple(as_simple_sku_id),

  afas_created                    timestamptz          NOT NULL  DEFAULT now(),
  afas_created_by                 text                 NOT NULL,
  afas_last_modified              timestamptz          NOT NULL  DEFAULT now(),
  afas_last_modified_by           text                 NOT NULL,
  afas_flow_id                    text                 NOT NULL,
  afas_version                    integer              NOT NULL,

  afas_initial_purchase_price     integer              NULL,
  afas_initial_purchase_currency  zz_commons.currency  NULL,
  afas_last_purchase_price        integer              NULL,
  afas_last_purchase_currency     zz_commons.currency  NULL,
  afas_valuation_price            integer              NULL,
  afas_valuation_currency         zz_commons.currency  NULL,
  afas_landed_cost_price          integer              NULL,
  afas_landed_cost_currency       zz_commons.currency  NULL,
  afas_amortization_rate          integer              NULL,

  CONSTRAINT article_facet_accounting_simple_sku_id_check CHECK (zcat_data.is_simple_sku_id(afas_simple_sku_id)),

  CONSTRAINT article_facet_accounting_simple_initial_purchase_currency_check
  CHECK (afas_initial_purchase_price IS NULL OR afas_initial_purchase_currency IS NOT NULL),

  CONSTRAINT article_facet_accounting_simple_last_purchase_currency_check
  CHECK (afas_last_purchase_price IS NULL OR afas_last_purchase_currency IS NOT NULL),

  CONSTRAINT article_facet_accounting_simple_valuation_currency_check
  CHECK (afas_valuation_price IS NULL OR afas_valuation_currency IS NOT NULL),

  CONSTRAINT article_facet_accounting_simple_landed_cost_currency_check
  CHECK (afas_landed_cost_price IS NULL OR afas_landed_cost_currency IS NOT NULL),

  CONSTRAINT article_facet_accounting_simple_amortization_rate_check
  CHECK (afas_amortization_rate IS NULL OR (afas_amortization_rate > 0 AND afas_amortization_rate < 100))
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_accounting_simple'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_accounting_simple.afas_landed_cost_price IS 'Is the sum of purchase price and
additional costs. Only used by zLabels.';
