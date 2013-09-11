create type supplier_model as (
  version           integer,
  model_sku         text,
  supplier_code     text,
  article_name      text,
  article_code      text,
  shoe_last_group   text,
  config_facets     supplier_config[]
);
