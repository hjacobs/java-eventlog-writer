create table zcat_commons.price_level (
    pl_id              smallint primary key,
    pl_level           smallint unique,
    pl_name            text,
    pl_is_promotional  boolean not null default false,
    pl_is_layouted     boolean not null,
    pl_is_fallback     boolean not null
);

insert into zcat_commons.price_level
    (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)
values
    (1, 50, 'standard', false, true, true),
    (2, 75, 'strategic', false, true, true),
    (3, 100, 'blocked_price', false, true, true),
    (4, 125, 'promotional', true, true, true),
    (5, 150, 'blocked_promotional', true, true, true),
    (6, 0, 'base_fallback', false, true, true), -- ZEOS-9327 base fallback price level used as as special case for price definitions
                                                -- that should be used during materialization as a last resort fallback
    (7, 60, 'no_layout_standard', false, false, false),
    (8, 170, 'no_layout_promotional', true, false, false),
    (9, 65, 'no_fallback_standard', false, true, false),
    (10, 180, 'no_fallback_promotional', true, true, false),
    (11, 110, 'risk_promotional', true, false, false),
    (12, 175, 'gift_voucher', false, false, false);

create index on zcat_commons.price_level (pl_level);
