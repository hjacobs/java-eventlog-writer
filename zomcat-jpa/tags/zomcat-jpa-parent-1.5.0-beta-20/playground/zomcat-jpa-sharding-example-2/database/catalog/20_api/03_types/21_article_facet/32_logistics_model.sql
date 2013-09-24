create type logistics_model as (
  version               integer,
  model_sku             text,
  country_of_origin     zz_commons.country_code,
  bootleg_type_code     option_value_type_code,
  customs_code          character(11),
  alcohol_strength      integer,
  config_facets         logistics_config[]
);
