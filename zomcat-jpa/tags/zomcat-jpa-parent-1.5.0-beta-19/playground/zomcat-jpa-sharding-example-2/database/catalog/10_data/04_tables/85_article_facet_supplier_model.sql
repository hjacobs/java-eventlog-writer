
CREATE TABLE zcat_data.article_facet_supplier_model (
  afsm_model_sku_id      integer      NOT NULL  REFERENCES zcat_data.article_model(am_model_sku_id),
  afsm_supplier_code     text         NOT NULL,

  afsm_created           timestamptz  NOT NULL  DEFAULT now(),
  afsm_created_by        text         NOT NULL,
  afsm_last_modified     timestamptz  NOT NULL  DEFAULT now(),
  afsm_last_modified_by  text         NOT NULL,
  afsm_flow_id           text         NOT NULL,
  afsm_version           integer      NOT NULL,

  afsm_article_name      text         NULL,
  afsm_article_code      text         NULL,

  afsm_shoe_last_group   text         NULL,

  PRIMARY KEY (afsm_model_sku_id, afsm_supplier_code),

  CONSTRAINT article_facet_supplier_model_sku_id_check CHECK (zcat_data.is_model_sku_id(afsm_model_sku_id))
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_supplier_model'::regclass);
