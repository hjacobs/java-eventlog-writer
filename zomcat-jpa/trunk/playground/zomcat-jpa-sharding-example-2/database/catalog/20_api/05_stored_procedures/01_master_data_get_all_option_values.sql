CREATE OR REPLACE FUNCTION master_data_get_all_option_values()
returns SETOF option_value as
$BODY$
BEGIN
  RETURN QUERY
  SELECT
      ROW(
      upper(p.relname)::zcat_commons.option_type,
      t.ov_code
    )::option_value_type_code,
    t.ov_name_message_key,
    t.ov_is_active,
    t.ov_sort_key
  FROM zcat_option_value.option_value as t
    JOIN pg_class As p ON t.tableoid = p.oid
  ORDER BY ov_sort_key;


END
$BODY$
language plpgsql
    STABLE security definer
    cost 100;