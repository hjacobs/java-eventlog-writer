create type article_simple as (
    version                 integer,
    simple_sku              text,
    zalando_article         boolean,
    partner_article         boolean,
    globally_rebateable     boolean,
    risk_article            boolean,
    ean                     text,
    previous_eans           text[],
    sales_channels_release  sales_channels_release,
    size_codes              size_code[]
);
