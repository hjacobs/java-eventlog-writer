create table zcat_data.price_definition (
    pd_id                           bigserial primary key,
    pd_sku_id                       int references zcat_data.article_sku(as_id) not null,
    pd_price_level_id               smallint not null references zcat_commons.price_level,
    pd_country_code                 zz_commons.country_code not null,
    pd_start_date                   timestamptz not null,
    pd_end_date                     timestamptz not null,
    pd_price                        integer,
    pd_pct_value                    integer,
    pd_appdomain_id                 smallint references zz_commons.appdomain (ad_id),
    pd_partner_id                   integer,
    pd_price_level_reason_code_id   smallint references zcat_data.price_level_reason_code (plrc_id),

    CONSTRAINT promotional_pct_price CHECK (
        (pd_pct_value is not null and pd_price is null) or
        (pd_pct_value is null and pd_price is not null)
    ),
    CONSTRAINT countries_with_multiple_appdomains CHECK (
        pd_appdomain_id not in (21, 26, 37)
    ),
    CONSTRAINT start_date_smaller_than_end_date CHECK (
      pd_end_date > pd_start_date
    )
);

create index on zcat_data.price_definition (pd_sku_id, pd_appdomain_id, pd_start_date, pd_end_date);
-- here separate indexes are needed.
create index on zcat_data.price_definition (pd_start_date);
create index on zcat_data.price_definition (pd_end_date);
comment on column zcat_data.price_definition.pd_pct_value is 'Percentage of the promotional price compared to the base price, ie: 70% the price.';
comment on column zcat_data.price_definition.pd_appdomain_id is 'Because of ZEOS-9312 appdomains: 21, 26, 37 are not allowed.';
