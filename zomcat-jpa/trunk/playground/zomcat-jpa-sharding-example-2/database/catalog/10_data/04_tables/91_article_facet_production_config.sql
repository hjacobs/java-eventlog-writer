CREATE TABLE zcat_data.article_facet_production_config (
  afpc_config_sku_id        integer                       NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_config(ac_config_sku_id),

  afpc_created              timestamptz                   NOT NULL  DEFAULT now(),
  afpc_created_by           text                          NOT NULL,
  afpc_last_modified        timestamptz                   NOT NULL  DEFAULT now(),
  afpc_last_modified_by     text                          NOT NULL,
  afpc_flow_id              text                          NOT NULL,
  afpc_version              integer                       NOT NULL,

  afpc_lead_time_days       integer                       NULL,
  afpc_material_id          int references zcat_option_value.production_material(ov_id) NULL,

  CONSTRAINT article_facet_production_config_sku_id_check CHECK (zcat_data.is_config_sku_id(afpc_config_sku_id)),
  CONSTRAINT article_facet_production_config_lead_time_days_check CHECK (afpc_lead_time_days IS NULL OR afpc_lead_time_days between 0 AND 999)
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_production_config'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_production_config.afpc_lead_time_days
     IS 'Time, that is needed to produce the article. In days';