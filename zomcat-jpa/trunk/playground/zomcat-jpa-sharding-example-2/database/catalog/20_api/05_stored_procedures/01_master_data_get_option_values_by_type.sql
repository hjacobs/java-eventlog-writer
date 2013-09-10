CREATE OR REPLACE FUNCTION master_data_get_option_values_by_type(p_type zcat_commons.option_type)
returns SETOF option_value as
$BODY$
BEGIN

    RETURN QUERY
        SELECT
              ROW(
                p_type,
                t.ov_code
               )::option_value_type_code, -- (select master_data_get_option_value_type_code_by_id(ov_id)),
               t.ov_name_message_key,
               t.ov_is_active,
               t.ov_sort_key
          FROM zcat_option_value.option_value as t
          JOIN pg_class As p ON t.tableoid = p.oid
         WHERE p.relname = lower(p_type::text)
         ORDER BY ov_sort_key;

END
$BODY$
language plpgsql
    STABLE security definer
    cost 100;