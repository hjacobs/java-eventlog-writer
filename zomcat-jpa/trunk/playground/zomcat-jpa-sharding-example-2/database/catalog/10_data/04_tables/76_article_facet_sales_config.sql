CREATE TABLE zcat_data.article_facet_sales_config (
  afsc_config_sku_id               integer                               NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_config(ac_config_sku_id),

  afsc_created                     timestamptz                           NOT NULL  DEFAULT now(),
  afsc_created_by                  text                                  NOT NULL,
  afsc_last_modified               timestamptz                           NOT NULL  DEFAULT now(),
  afsc_last_modified_by            text                                  NOT NULL,
  afsc_flow_id                     text                                  NOT NULL,
  afsc_version                     integer                               NOT NULL,

  afsc_comment                     text                                  NULL,
  afsc_sole_type_id                int references zcat_option_value.sole_type(ov_id) NULL,
  afsc_insole_type_id              int references zcat_option_value.insole_type(ov_id) NULL,
  afsc_trend1_id                   int references zcat_option_value.trend(ov_id) NULL,
  afsc_trend2_id                   int references zcat_option_value.trend(ov_id) NULL,
  afsc_textile_upper_id            int references zcat_option_value.textile_upper(ov_id) NULL,
  afsc_shoe_upper_id               int references zcat_option_value.shoe_upper(ov_id) NULL,
  afsc_target_group_age_id         int references zcat_option_value.target_group_age(ov_id) NULL,
  afsc_lining_type_id              int references zcat_option_value.lining_type(ov_id) NULL,
  afsc_shoe_lining_material_id     int references zcat_option_value.shoe_lining_material(ov_id) NULL,
  afsc_textile_lining_material_id  int references zcat_option_value.textile_lining_material(ov_id) NULL,
  afsc_leather_type_id             int references zcat_option_value.leather_type(ov_id) NULL,

  CONSTRAINT article_facet_sales_config_sku_id_check CHECK (zcat_data.is_config_sku_id(afsc_config_sku_id)),

  CONSTRAINT article_facet_sales_config_trend_not_twice_check
       CHECK ((afsc_trend1_id IS NULL AND afsc_trend2_id IS NULL)
              OR afsc_trend1_id IS DISTINCT FROM afsc_trend2_id)
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_sales_config'::regclass);
