create type accounting_model as (
  version                                   integer,
  model_sku                                 text,
  value_added_tax_classification_type_code  option_value_type_code,
  input_tax_classification_type_code        option_value_type_code,
  config_facets                             accounting_config[]
);
