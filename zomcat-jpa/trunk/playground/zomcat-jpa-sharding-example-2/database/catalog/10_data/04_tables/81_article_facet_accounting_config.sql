
CREATE TABLE zcat_data.article_facet_accounting_config(
  afac_config_sku_id              integer              NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_config(ac_config_sku_id),

  afac_created                    timestamptz          NOT NULL  DEFAULT now(),
  afac_created_by                 text                 NOT NULL,
  afac_last_modified              timestamptz          NOT NULL  DEFAULT now(),
  afac_last_modified_by           text                 NOT NULL,
  afac_flow_id                    text                 NOT NULL,
  afac_version                    integer              NOT NULL,

  afac_initial_purchase_price     integer              NULL,
  afac_initial_purchase_currency  zz_commons.currency  NULL,
  afac_last_purchase_price        integer              NULL,
  afac_last_purchase_currency     zz_commons.currency  NULL,
  afac_valuation_price            integer              NULL,
  afac_valuation_currency         zz_commons.currency  NULL,
  afac_landed_cost_price          integer              NULL,
  afac_landed_cost_currency       zz_commons.currency  NULL,
  afac_amortization_rate          integer              NULL,

  CONSTRAINT article_facet_accounting_config_sku_id_check CHECK (zcat_data.is_config_sku_id(afac_config_sku_id)),

  CONSTRAINT article_facet_accounting_config_initial_purchase_currency_check
       CHECK (afac_initial_purchase_price IS NULL OR afac_initial_purchase_currency IS NOT NULL),

  CONSTRAINT article_facet_accounting_config_last_purchase_currency_check
       CHECK (afac_last_purchase_price IS NULL OR afac_last_purchase_currency IS NOT NULL),

  CONSTRAINT article_facet_accounting_config_valuation_currency_check
       CHECK (afac_valuation_price IS NULL OR afac_valuation_currency IS NOT NULL),

  CONSTRAINT article_facet_accounting_config_landed_cost_currency_check
       CHECK (afac_landed_cost_price IS NULL OR afac_landed_cost_currency IS NOT NULL),

  CONSTRAINT article_facet_accounting_config_amortization_rate_check
       CHECK (afac_amortization_rate IS NULL OR (afac_amortization_rate > 0 AND afac_amortization_rate < 100))
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_accounting_config'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_accounting_config.afac_landed_cost_price IS 'Is the sum of purchase price and
additional costs. Only used by zLabels.';

