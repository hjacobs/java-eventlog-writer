create type production_config as (
  version                       integer,
  config_sku                    text,
  lead_time                     integer,
  production_material_type_code option_value_type_code,
  simple_facets                 production_simple[]
);
