create table zcat_data.price_archive (
    pa_id                                               bigserial primary key,
    pa_simple_sku_id                                    int references zcat_data.article_sku(as_id) not null,
    pa_app_domain_id                                    smallint not null,
    pa_partner_id                                       integer,
    pa_price                                            integer,
    pa_promotional_price                                integer,
    pa_source_sku_id                                    integer references zcat_data.article_sku(as_id),
    pa_source_country_code                              zz_commons.country_code,
    pa_source_app_domain_id                             smallint,
    pa_source_price_level_id                            integer references zcat_commons.price_level,
    pa_source_price_end_date                            timestamptz,
    pa_source_promotional_sku_id                        integer references zcat_data.article_sku(as_id),
    pa_source_promotional_country_code                  zz_commons.country_code,
    pa_source_promotional_app_domain_id                 smallint,
    pa_source_promotional_price_level_id                integer references zcat_commons.price_level,
    pa_source_promotional_price_end_date                timestamptz,
    pa_created                                          timestamptz not null default now(),
    pa_violated_rules                                   text,
    pa_source_price_level_reason_code_id                integer references zcat_data.price_level_reason_code(plrc_id),
    pa_source_promotional_price_level_reason_code_id    integer references zcat_data.price_level_reason_code(plrc_id),
    pa_risk_promotional_price                           integer,
    pa_risk_promotional_price_level_reason_code_id      integer references zcat_data.price_level_reason_code(plrc_id),
    pa_source_price_start_date                          timestamptz,
    pa_source_promotional_price_start_date              timestamptz
);

comment on table zcat_data.price_archive IS 'Table to store archive prices.';
