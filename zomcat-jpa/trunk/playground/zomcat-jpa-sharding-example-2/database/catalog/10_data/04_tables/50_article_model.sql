
CREATE TABLE zcat_data.article_model (
    am_model_sku_id                  integer      NOT NULL PRIMARY KEY REFERENCES zcat_data.article_sku(as_id),

    am_created                       timestamptz  NOT NULL  DEFAULT now(),
    am_created_by                    text         NOT NULL,
    am_last_modified                 timestamptz  NOT NULL  DEFAULT now(),
    am_last_modified_by              text         NOT NULL,
    am_flow_id                       text         NOT NULL,
    am_version                       integer      NOT NULL,

    am_name                          text         NOT NULL,
    am_brand_code                    text         NOT NULL  REFERENCES zcat_commons.brand(b_code),
    am_commodity_group_code          text         NOT NULL  REFERENCES zcat_commons.commodity_group(cg_code),
    am_target_group_set              bigint       NOT NULL  DEFAULT 0,
    am_size_chart_group_id           integer      NOT NULL  REFERENCES zcat_commons.size_chart_group (scg_id),
    am_description                   text         NULL,
    am_main_supplier_code            text         NULL,
    am_is_globally_rebateable        boolean      NULL,
    am_is_risk_article               boolean      NULL,
    am_is_commission_article         boolean      NOT NULL DEFAULT false,

    CONSTRAINT article_model_sku_id_check CHECK (zcat_data.is_model_sku_id(am_model_sku_id))
);

CREATE UNIQUE INDEX article_model_sku_id_uidx ON zcat_data.article_model(am_model_sku_id);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_model'::regclass);


COMMENT ON COLUMN zcat_data.article_model.am_target_group_set
    IS 'This field is a bitmap. The flags here show whether this article is for men or woman, which age group and article domain.
        More details are in zalando-domain/TargetGroup';

COMMENT ON COLUMN zcat_data.article_model.am_is_globally_rebateable IS 'Shows whether an article can receive a rebate in all countries.';
