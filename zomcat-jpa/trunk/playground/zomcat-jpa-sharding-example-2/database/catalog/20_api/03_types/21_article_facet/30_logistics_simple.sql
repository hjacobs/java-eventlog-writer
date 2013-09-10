create type logistics_simple as (
  version                       integer,
  simple_sku                    text,
  country_of_origin             zz_commons.country_code,
  customs_code                  character(11),
  net_weight                    integer,
  gross_weight                  integer,
  net_volume                    integer,
  gross_volume                  integer,
  shipping_placement_type_code  option_value_type_code,
  package_height                integer,
  package_width                 integer,
  package_length                integer,
  customs_notification_required boolean,
  preferential_treatment        boolean
);
