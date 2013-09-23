create type price_configuration_timecut as (
    effective_price_definitions price_definition [],
    target_appdomain_id         smallint,
    effective_date              timestamptz
);
