create type price_level as (
    id                  smallint,
    level               smallint,
    name                text,
    promotional         boolean,
    is_layouted         boolean,
    is_fallback         boolean
);
