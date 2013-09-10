create type sales_config as (
  version                           integer,
  config_sku                        text,
  comment                           text,
  sole_type_code                    option_value_type_code,
  insole_type_code                  option_value_type_code,
  trend1_type_code                  option_value_type_code,
  trend2_type_code                  option_value_type_code,
  textile_upper_type_code           option_value_type_code,
  shoe_upper_type_code              option_value_type_code,
  target_group_age_type_code        option_value_type_code,
  lining_type_code                  option_value_type_code,
  shoe_lining_material_type_code    option_value_type_code,
  textile_lining_material_type_code option_value_type_code,
  leather_type_code                 option_value_type_code,
  simple_facets                     sales_simple[]
);
