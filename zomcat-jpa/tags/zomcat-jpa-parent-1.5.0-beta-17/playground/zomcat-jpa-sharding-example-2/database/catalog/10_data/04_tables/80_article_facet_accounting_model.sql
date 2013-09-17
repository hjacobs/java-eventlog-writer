CREATE TABLE zcat_data.article_facet_accounting_model(
  afam_model_sku_id                       integer                                         NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_model(am_model_sku_id),

  afam_created                            timestamptz                                     NOT NULL  DEFAULT now(),
  afam_created_by                         text                                            NOT NULL,
  afam_last_modified                      timestamptz                                     NOT NULL  DEFAULT now(),
  afam_last_modified_by                   text                                            NOT NULL,
  afam_flow_id                            text                                            NOT NULL,
  afam_version                            integer                                         NOT NULL,

  afam_value_added_tax_classification_id  int references zcat_option_value.tax_classification(ov_id) NULL,
  afam_input_tax_classification_id        int references zcat_option_value.tax_classification(ov_id) NULL,

  CONSTRAINT article_facet_accounting_model_sku_id_check CHECK (zcat_data.is_model_sku_id(afam_model_sku_id))
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_accounting_model'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_accounting_model.afam_value_added_tax_classification_id
     IS 'In German: Mehrwertsteuer-Klassifikation';

COMMENT ON COLUMN zcat_data.article_facet_accounting_model.afam_input_tax_classification_id
     IS 'In German: Vorsteuer-Klassifikation';
