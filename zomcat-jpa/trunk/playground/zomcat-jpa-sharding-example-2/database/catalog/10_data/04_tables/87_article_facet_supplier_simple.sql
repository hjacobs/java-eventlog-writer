
CREATE TABLE zcat_data.article_facet_supplier_simple (
  afss_simple_sku_id     integer      NOT NULL  REFERENCES zcat_data.article_simple(as_simple_sku_id),
  afss_supplier_code     text         NOT NULL,

  afss_created           timestamptz  NOT NULL  DEFAULT now(),
  afss_created_by        text         NOT NULL,
  afss_last_modified     timestamptz  NOT NULL  DEFAULT now(),
  afss_last_modified_by  text         NOT NULL,
  afss_flow_id           text         NOT NULL,
  afss_version           integer      NOT NULL,

  afss_article_code      text         NULL,

  PRIMARY KEY (afss_simple_sku_id, afss_supplier_code),

  CONSTRAINT article_facet_supplier_simple_sku_id_check CHECK (zcat_data.is_simple_sku_id(afss_simple_sku_id))
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_supplier_simple'::regclass);
