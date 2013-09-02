CREATE TYPE price_configuration_update AS (
    price_definitions price_definition[],
    price_definitions_to_delete bigint[]
);