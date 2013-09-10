CREATE TABLE zcat_commons.brand_size_chart_group (
    bscg_created                timestamptz  NOT NULL  DEFAULT now(),
    bscg_created_by             text         NOT NULL,
    bscg_last_modified          timestamptz  NOT NULL  DEFAULT now(),
    bscg_last_modified_by       text         NOT NULL,
    bscg_flow_id                text         NOT NULL,

    bscg_brand_code             text         NOT NULL references zcat_commons.brand(b_code),
    bscg_size_chart_group       integer      NOT NULL references zcat_commons.size_chart_group(scg_id),
    bscg_commodity_group_code   text             NULL references zcat_commons.commodity_group(cg_code),
    bscg_is_active              boolean      NOT NULL DEFAULT true
);

CREATE UNIQUE INDEX bscg_brand_size_chart_group_commodity_group_code_idx
    ON zcat_commons.brand_size_chart_group(bscg_brand_code, bscg_size_chart_group, bscg_commodity_group_code);
CREATE UNIQUE INDEX bscg_brand_size_chart_group_null_idx
    ON zcat_commons.brand_size_chart_group (bscg_brand_code, bscg_size_chart_group)
 WHERE bscg_commodity_group_code IS NULL;

COMMENT ON TABLE zcat_commons.brand_size_chart_group IS '
This table contains n<->m mappings between brands and size charts that may be filtered by commoditiy groups.';

COMMENT ON COLUMN zcat_commons.brand_size_chart_group.bscg_brand_code IS '
References the brand.';

COMMENT ON COLUMN zcat_commons.brand_size_chart_group.bscg_size_chart_group IS '
References the size chart group.';

COMMENT ON COLUMN zcat_commons.brand_size_chart_group.bscg_commodity_group_code IS '
References an optional commodity group.';

