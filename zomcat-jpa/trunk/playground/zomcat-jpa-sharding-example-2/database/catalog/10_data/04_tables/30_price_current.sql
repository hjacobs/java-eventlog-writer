create table zcat_data.price_current (
    pc_id                                               bigserial primary key,
    pc_simple_sku_id                                    int references zcat_data.article_sku(as_id) not null,
    pc_app_domain_id                                    smallint not null,
    pc_partner_id                                       integer,
    pc_price                                            integer,
    pc_promotional_price                                integer,
    pc_source_sku_id                                    integer references zcat_data.article_sku(as_id),
    pc_source_country_code                              zz_commons.country_code,
    pc_source_app_domain_id                             smallint,
    pc_source_price_level_id                            integer references zcat_commons.price_level,
    pc_source_price_end_date                            timestamptz,
    pc_source_promotional_sku_id                        integer references zcat_data.article_sku(as_id),
    pc_source_promotional_country_code                  zz_commons.country_code,
    pc_source_promotional_app_domain_id                 smallint,
    pc_source_promotional_price_level_id                integer references zcat_commons.price_level,
    pc_source_promotional_price_end_date                timestamptz,
    pc_created                                          timestamptz not null default now(),
    pc_violated_rules                                   text,
    pc_last_modified                                    timestamptz not null default now(),
    pc_source_price_level_reason_code_id                integer references zcat_data.price_level_reason_code(plrc_id),
    pc_source_promotional_price_level_reason_code_id    integer references zcat_data.price_level_reason_code(plrc_id),
    pc_risk_promotional_price                           integer,
    pc_risk_promotional_price_level_reason_code_id      integer references zcat_data.price_level_reason_code(plrc_id),
    pc_source_price_start_date                          timestamptz,
    pc_source_promotional_price_start_date              timestamptz
);

create unique index on zcat_data.price_current (pc_simple_sku_id, pc_app_domain_id, pc_partner_id);
create unique index on zcat_data.price_current (pc_simple_sku_id, pc_app_domain_id) where pc_partner_id is null;
create index on zcat_data.price_current (pc_created);

comment on table zcat_data.price_current IS 'Table to store current prices.';
