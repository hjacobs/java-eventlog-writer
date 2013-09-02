create type supplier_container_config as (
    version         integer,
    config_sku      text,
    simple_facets   supplier_container_simple[],
    suppliers       supplier_config[]
);
