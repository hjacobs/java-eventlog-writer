create type production_model as (
  version                       integer,
  model_sku                     text,
  quality_group_q               text,
  type_code_q                   option_value_type_code,
  material_weight               integer,
  mesh                          integer,
  material_detail_type_code     option_value_type_code,
  config_facets                 production_config[]
);
