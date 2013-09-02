create type article_model as (
    version                       integer,
    model_sku                     text,
    name                          text,
    brand_code                    text,
    commodity_group_code          text,
    target_group_set              bigint,
    description                   text,
    size_chart_codes              text[],
    main_supplier_code            text,
    globally_rebateable           boolean,
    risk_article                  boolean,
    commission_article            boolean,
    sales_channels_release        sales_channels_release,
    config_facets                 article_config[]
);
