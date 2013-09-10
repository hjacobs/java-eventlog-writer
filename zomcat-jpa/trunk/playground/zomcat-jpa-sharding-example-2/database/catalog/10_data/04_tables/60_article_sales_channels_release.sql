CREATE TABLE zcat_data.article_sales_channels_release (
    ascr_article_sku_id      integer      NOT NULL  REFERENCES zcat_data.article_sku(as_id),

    ascr_created             timestamptz  NOT NULL  DEFAULT now(),
    ascr_created_by          text         NOT NULL,
    ascr_last_modified       timestamptz  NOT NULL  DEFAULT now(),
    ascr_last_modified_by    text         NOT NULL,
    ascr_flow_id             text         NOT NULL,
    ascr_version             integer      NOT NULL,

    ascr_is_lounge           boolean      NULL,
    ascr_is_offline_outlet   boolean      NULL,
    ascr_is_resale           boolean      NULL,
    ascr_is_emeza            boolean      NULL,
    ascr_is_kiomi            boolean      NULL,

    PRIMARY KEY (ascr_article_sku_id)
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_sales_channels_release'::regclass);

COMMENT ON TABLE zcat_data.article_sales_channels_release IS 'This table is used to release articles for certain
                                        sales channels, e.g. for lounge, offline outlet, resale.
                                        Sales channels can be set on model, config and simple level and are inherited.';