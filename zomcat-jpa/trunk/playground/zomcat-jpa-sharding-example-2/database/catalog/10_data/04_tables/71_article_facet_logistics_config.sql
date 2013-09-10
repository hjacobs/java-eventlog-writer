CREATE TABLE zcat_data.article_facet_logistics_config (
  aflc_config_sku_id                 integer                          NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_config(ac_config_sku_id),

  aflc_created                       timestamptz                      NOT NULL  DEFAULT now(),
  aflc_created_by                    text                             NOT NULL,
  aflc_last_modified                 timestamptz                      NOT NULL  DEFAULT now(),
  aflc_last_modified_by              text                             NOT NULL,
  aflc_flow_id                       text                             NOT NULL,
  aflc_version                       integer                          NOT NULL,

  aflc_is_cage_product               boolean                          NOT NULL  DEFAULT FALSE,
  aflc_is_fragile                    boolean                          NOT NULL  DEFAULT FALSE,
  aflc_is_oversized_package          boolean                          NOT NULL  DEFAULT FALSE,
  aflc_is_extra_heavy                boolean                          NOT NULL  DEFAULT FALSE,
  aflc_is_not_individually_packed    boolean                          NOT NULL  DEFAULT FALSE,
  aflc_has_no_packaging              boolean                          NOT NULL  DEFAULT FALSE,
  aflc_has_unsafe_packaging          boolean                          NOT NULL  DEFAULT FALSE,
  aflc_is_ce_certified               boolean                          NOT NULL  DEFAULT FALSE,
  aflc_is_hanging_garment            boolean                          NOT NULL  DEFAULT FALSE,
  aflc_is_functional_test_required   boolean                          NOT NULL  DEFAULT FALSE,
  aflc_is_hazardous                  boolean                          NULL,
  aflc_article_set_size              integer                          NULL,
  aflc_is_perishable                 boolean                          NULL,
  aflc_country_of_origin             zz_commons.country_code          NULL,
  aflc_customs_code                  character(11)                    NULL,
  aflc_net_weight                    integer                          NULL,
  aflc_gross_weight                  integer                          NULL,
  aflc_net_volume                    integer                          NULL,
  aflc_gross_volume                  integer                          NULL,
  aflc_shipping_placement_id         int references zcat_option_value.shipping_placement(ov_id) NULL,
  aflc_is_comes_as_set               boolean                          NOT NULL  DEFAULT FALSE

  CONSTRAINT article_facet_logistics_config_sku_id_check CHECK (zcat_data.is_config_sku_id(aflc_config_sku_id)),

  CONSTRAINT article_facet_logistics_config_customs_code_check CHECK (aflc_customs_code ~ '^[0-9]{11}$')

);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_logistics_config'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_customs_code IS 'The eleven-character number is used for import, the first eight characters are used for export.';
COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_net_weight IS 'Weight without package; in gram';
COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_gross_weight IS 'Weight with package; in gram';
COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_net_volume IS 'Volume without package; in cc (cubic centimeter)';
COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_gross_volume IS 'Volume with package; in cc (cubic centimeter)';
COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_is_cage_product IS 'Valuable articles, that are easy to steal (e.g. gold rings) are locked in cages';
COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_is_hanging_garment IS 'If true, the article must be hung up.';
COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_is_not_individually_packed IS 'If true, one article cannot be picked separately from its package. There are several articles in one package.';
COMMENT ON COLUMN zcat_data.article_facet_logistics_config.aflc_article_set_size IS 'If not null, the article comes as a set of size x.';
