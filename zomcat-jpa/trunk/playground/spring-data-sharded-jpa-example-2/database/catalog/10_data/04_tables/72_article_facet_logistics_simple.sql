CREATE TABLE zcat_data.article_facet_logistics_simple (
  afls_simple_sku_id                     integer                          NOT NULL  PRIMARY KEY  REFERENCES zcat_data.article_simple(as_simple_sku_id),

  afls_created                           timestamptz                      NOT NULL  DEFAULT now(),
  afls_created_by                        text                             NOT NULL,
  afls_last_modified                     timestamptz                      NOT NULL  DEFAULT now(),
  afls_last_modified_by                  text                             NOT NULL,
  afls_flow_id                           text                             NOT NULL,
  afls_version                           integer                          NOT NULL,

  afls_is_customs_notification_required  boolean                          NOT NULL  DEFAULT FALSE,
  afls_has_preferential_treatment        boolean                          NOT NULL  DEFAULT FALSE,

  afls_country_of_origin                 zz_commons.country_code          NULL,
  afls_customs_code                      character(11)                    NULL,
  afls_net_weight                        integer                          NULL,
  afls_gross_weight                      integer                          NULL,
  afls_net_volume                        integer                          NULL,
  afls_gross_volume                      integer                          NULL,
  afls_shipping_placement_id             int references zcat_option_value.shipping_placement(ov_id) NULL,
  afls_package_height                    integer                          NULL,
  afls_package_width                     integer                          NULL,
  afls_package_length                    integer                          NULL,

  CONSTRAINT article_facet_logistics_simple_sku_id_check CHECK (zcat_data.is_simple_sku_id(afls_simple_sku_id)),

  CONSTRAINT article_facet_logistics_simple_customs_code_check CHECK (afls_customs_code ~ '^[0-9]{11}$')
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_logistics_simple'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_logistics_simple.afls_net_weight IS 'Weight without package; in gram';
COMMENT ON COLUMN zcat_data.article_facet_logistics_simple.afls_gross_weight IS 'Weight with package; in gram';
COMMENT ON COLUMN zcat_data.article_facet_logistics_simple.afls_net_volume IS 'Volume without package; in cc (cubic centimeter)';
COMMENT ON COLUMN zcat_data.article_facet_logistics_simple.afls_gross_volume IS 'Volume with package; in cc (cubic centimeter)';
COMMENT ON COLUMN zcat_data.article_facet_logistics_simple.afls_package_height IS 'In centimeter';
COMMENT ON COLUMN zcat_data.article_facet_logistics_simple.afls_package_width IS 'In centimeter';
COMMENT ON COLUMN zcat_data.article_facet_logistics_simple.afls_package_length IS 'In centimeter';
