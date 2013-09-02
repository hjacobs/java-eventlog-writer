create type multimedia_model as (
    version               integer,
    model_sku             text,
    shop_multimedia       shop_multimedia[],
    config_facets         multimedia_config[]
);
