create type supplier_config as (
  version                       integer,
  config_sku                    text,
  supplier_code                 text,
  article_code                  text,
  color_code                    text,
  color_description             text,
  availability_type_code        option_value_type_code,
  upper_material_description    text,
  lining_description            text,
  sole_description              text,
  inner_sole_description        text,
  simple_facets                 supplier_simple[]
);
