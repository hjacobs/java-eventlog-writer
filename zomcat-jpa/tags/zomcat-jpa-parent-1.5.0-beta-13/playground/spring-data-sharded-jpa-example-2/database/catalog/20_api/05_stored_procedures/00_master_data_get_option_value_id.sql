CREATE OR REPLACE FUNCTION master_data_get_option_value_id(p_option_value option_value)
returns setof int as
$BODY$
BEGIN

    RETURN QUERY
      SELECT ov_id
      FROM zcat_option_value.option_value AS t
      JOIN pg_class As p ON t.tableoid = p.oid
      WHERE p.relname = lower(p_option_value.option_type::text) AND ov_code = p_option_value.code;

END
$BODY$
language plpgsql
    STABLE security definer
    cost 100;