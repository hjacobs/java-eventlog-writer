CREATE TABLE zcat_data.article_config (
    ac_config_sku_id                 integer                    NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_sku(as_id),

    ac_created                       timestamptz                NOT NULL  DEFAULT now(),
    ac_created_by                    text                       NOT NULL,
    ac_last_modified                 timestamptz                NOT NULL  DEFAULT now(),
    ac_last_modified_by              text                       NOT NULL,
    ac_flow_id                       text                       NOT NULL,
    ac_version                       integer                    NOT NULL,

    ac_first_season_code             text                       NOT NULL  REFERENCES zcat_commons.season(s_code),
    ac_season_code                   text                       NOT NULL  REFERENCES zcat_commons.season(s_code),

    ac_main_color_code               text                       NOT NULL  REFERENCES zcat_commons.color(c_code),
    ac_is_reloaded_article           boolean                    NOT NULL  DEFAULT false,
    ac_is_keystyle                   boolean                    NOT NULL  DEFAULT false,
    ac_is_disposition_locked         boolean                    NOT NULL  DEFAULT false,

    ac_sub_season_id                 int references zcat_option_value.sub_season(ov_id) NULL,

    ac_second_color_code             text                       NULL      REFERENCES zcat_commons.color(c_code),
    ac_third_color_code              text                       NULL      REFERENCES zcat_commons.color(c_code),
    ac_main_material_code            text                       NULL      REFERENCES zcat_commons.material(m_code),

    ac_keystyle_delivery_date        timestamptz                NULL,

    ac_main_supplier_code            text                       NULL,
    ac_pattern_id                    int references zcat_option_value.pattern(ov_id) NULL,
    ac_is_globally_rebateable        boolean                    NULL,
    ac_is_risk_article               boolean                    NULL,
    ac_is_commission_article         boolean                    NULL,
    ac_is_key_value_item             boolean                    NULL,

    CONSTRAINT article_config_sku_id_check CHECK (zcat_data.is_config_sku_id(ac_config_sku_id)),
    CONSTRAINT article_config_first_season_check CHECK (ac_first_season_code ~ '^(FS|HW)[0-9]{2}$')
);

CREATE UNIQUE INDEX article_config_sku_id_uidx ON zcat_data.article_config(ac_config_sku_id);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_config'::regclass);


COMMENT ON COLUMN zcat_data.article_config.ac_is_globally_rebateable IS 'Shows whether an article can receive a rebate in all countries.';

