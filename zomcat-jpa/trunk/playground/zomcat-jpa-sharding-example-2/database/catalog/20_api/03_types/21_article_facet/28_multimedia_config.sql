create type multimedia_config as (
    version               integer,
    config_sku            text,
    shop_multimedia       shop_multimedia[],
    simple_facets         multimedia_simple[]
);
