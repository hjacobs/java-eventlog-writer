CREATE TYPE price_archive_entry AS (
    simple_sku                                      text,
    app_domain_id                                   smallint,
    partner_id                                      integer,
    price                                           integer,
    promotional_price                               integer,
    source_sku                                      text,
    source_country_code                             zz_commons.country_code,
    source_app_domain_id                            smallint,
    source_price_level_id                           integer,
    source_price_end_date                           timestamptz,
    source_promotional_sku                          text,
    source_promotional_country_code                 zz_commons.country_code,
    source_promotional_app_domain_id                smallint,
    source_promotional_price_level_id               integer,
    source_promotional_price_end_date               timestamptz,
    violated_rules                                  text,
    source_price_level_reason_code_id               integer,
    source_promotional_price_level_reason_code_id   integer,
    source_price_start_date                         timestamptz,
    source_promotional_price_start_date             timestamptz
);

