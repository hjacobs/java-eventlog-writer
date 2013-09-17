create type sku_price_definitions_pair as (
    sku                     text,
    sku_type                zcat_data.sku_type,
    price_definitions       price_definition[]
);
