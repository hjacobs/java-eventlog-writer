create type price_definition as (
    id                      bigint,
    sku                     text,
    price_level             price_level,
    price                   integer,
    appdomain_id            smallint,
    country_code            zz_commons.country_code,
    start_date              timestamptz,
    end_date                timestamptz,
    partner_id              integer,
    price_level_reason_code price_level_reason_code
);
