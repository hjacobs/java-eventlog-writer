create type supplier_container_model as (
    version         integer,
    model_sku       text,
    config_facets   supplier_container_config[],
    suppliers       supplier_model[]
);
