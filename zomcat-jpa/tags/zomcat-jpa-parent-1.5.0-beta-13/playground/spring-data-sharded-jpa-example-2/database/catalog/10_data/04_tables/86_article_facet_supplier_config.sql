CREATE TABLE zcat_data.article_facet_supplier_config (
  afsc_config_sku_id               integer                    NOT NULL  REFERENCES zcat_data.article_config(ac_config_sku_id),
  afsc_supplier_code               text                       NOT NULL,

  afsc_created                     timestamptz                NOT NULL  DEFAULT now(),
  afsc_created_by                  text                       NOT NULL,
  afsc_last_modified               timestamptz                NOT NULL  DEFAULT now(),
  afsc_last_modified_by            text                       NOT NULL,
  afsc_flow_id                     text                       NOT NULL,
  afsc_version                     integer                    NOT NULL,

  afsc_article_code                text                       NULL,
  afsc_color_code                  text                       NULL,
  afsc_color_description           text                       NULL,
  afsc_availability_id                int references zcat_option_value.availability(ov_id) NULL,

  afsc_upper_material_description  text                       NULL,
  afsc_lining_description          text                       NULL,
  afsc_sole_description            text                       NULL,
  afsc_inner_sole_description      text                       NULL,

  PRIMARY KEY (afsc_config_sku_id, afsc_supplier_code),

  CONSTRAINT article_facet_supplier_config_sku_id_check CHECK (zcat_data.is_config_sku_id(afsc_config_sku_id))
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_supplier_config'::regclass);
