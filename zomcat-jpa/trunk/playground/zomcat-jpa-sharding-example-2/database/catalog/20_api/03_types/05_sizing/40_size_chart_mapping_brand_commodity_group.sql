CREATE TYPE size_chart_mapping_brand_commodity_group AS (
  brand_code            text,
  commodity_group_code  text,
  size_chart_code_lists size_chart_code_list[]
);