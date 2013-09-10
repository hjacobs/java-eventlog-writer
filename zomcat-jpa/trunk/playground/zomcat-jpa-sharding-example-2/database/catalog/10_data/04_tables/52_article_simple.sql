CREATE TABLE zcat_data.article_simple (
    as_simple_sku_id            integer      NOT NULL   PRIMARY KEY REFERENCES zcat_data.article_sku(as_id),

    as_created                  timestamptz  NOT NULL   DEFAULT now(),
    as_created_by               text         NOT NULL,
    as_last_modified            timestamptz  NOT NULL   DEFAULT now(),
    as_last_modified_by         text         NOT NULL,
    as_flow_id                  text         NOT NULL,
    as_version                  integer      NOT NULL,

    as_is_zalando_article       boolean      NOT NULL DEFAULT false,
    as_is_partner_article       boolean      NOT NULL DEFAULT false,
    as_is_globally_rebateable   boolean      NULL,
    as_is_risk_article          boolean      NULL,

    CONSTRAINT article_simple_sku_id_check CHECK (zcat_data.is_simple_sku_id(as_simple_sku_id)),
    CONSTRAINT article_flags_at_least_one_vendor CHECK(as_is_zalando_article or as_is_partner_article)
);

CREATE UNIQUE INDEX article_simple_sku_id_uidx ON zcat_data.article_simple(as_simple_sku_id);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_simple'::regclass);

COMMENT ON COLUMN zcat_data.article_simple.as_is_globally_rebateable IS 'Shows whether an article can receive a rebate in all countries.';
