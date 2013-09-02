create type sales_model as (
  version                       integer,
  model_sku                     text,
  fitting_type_code             option_value_type_code,
  closure_type_code             option_value_type_code,
  toe_cap_type_code             option_value_type_code,
  sleeve_type_code              option_value_type_code,
  heel_height                   integer,
  heel_type_code                option_value_type_code,
  leg_type_code                 option_value_type_code,
  neck_line_type_code           option_value_type_code,
  shoe_upper_type_code          option_value_type_code,
  textile_membrane_type_code    option_value_type_code,
  fit_type_code                 option_value_type_code,
  extra_large                   boolean,
  sport_type_code               option_value_type_code,
  sub_sport_type_code           option_value_type_code,
  config_facets                 sales_config[]
);
