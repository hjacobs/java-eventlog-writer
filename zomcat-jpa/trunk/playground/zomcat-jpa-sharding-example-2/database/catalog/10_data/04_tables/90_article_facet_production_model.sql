CREATE TABLE zcat_data.article_facet_production_model (
  afpm_model_sku_id         integer                           NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_model(am_model_sku_id),

  afpm_created              timestamptz                       NOT NULL  DEFAULT now(),
  afpm_created_by           text                              NOT NULL,
  afpm_last_modified        timestamptz                       NOT NULL  DEFAULT now(),
  afpm_last_modified_by     text                              NOT NULL,
  afpm_flow_id              text                              NOT NULL,
  afpm_version              integer                           NOT NULL,

  afpm_quality_group_q      text                              NULL,
  afpm_type_q_id            int references zcat_option_value.type_q(ov_id) NULL,
  afpm_material_weight      integer                           NULL,
  afpm_mesh                 integer                           NULL,
  afpm_material_detail_id   int references zcat_option_value.material_detail(ov_id) NULL,

  CONSTRAINT article_facet_production_model_sku_id_check CHECK (zcat_data.is_model_sku_id(afpm_model_sku_id))
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_production_model'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_production_model.afpm_material_weight IS 'In gram per square meter';
COMMENT ON COLUMN zcat_data.article_facet_production_model.afpm_mesh IS 'In gg (Gauge)';
