CREATE TABLE zcat_data.article_facet_logistics_model (
  aflm_model_sku_id       integer                   NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_model(am_model_sku_id),

  aflm_created            timestamptz               NOT NULL  DEFAULT now(),
  aflm_created_by         text                      NOT NULL,
  aflm_last_modified      timestamptz               NOT NULL  DEFAULT now(),
  aflm_last_modified_by   text                      NOT NULL,
  aflm_flow_id            text                      NOT NULL,
  aflm_version            integer                   NOT NULL,

  aflm_country_of_origin  zz_commons.country_code   NULL,
  aflm_bootleg_type_id    int references zcat_option_value.bootleg_type(ov_id) NULL,
  aflm_customs_code       character(11)             NULL,
  aflm_alcohol_strength   integer                   NULL,

  CONSTRAINT article_facet_logistics_model_sku_id_check CHECK (zcat_data.is_model_sku_id(aflm_model_sku_id)),
  CONSTRAINT article_facet_logistics_model_customs_code_check CHECK (aflm_customs_code ~ '^[0-9]{11}$'),
  CONSTRAINT article_facet_logistics_model_alcohol_strength_check CHECK (aflm_alcohol_strength >= 0)
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_logistics_model'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_logistics_model.aflm_alcohol_strength
     IS 'Volume alcohol content; in per mill points (e.g. 59‰ = 5,9%)';
