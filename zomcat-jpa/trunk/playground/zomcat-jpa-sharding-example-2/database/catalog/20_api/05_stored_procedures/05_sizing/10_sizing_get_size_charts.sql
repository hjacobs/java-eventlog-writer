CREATE OR REPLACE FUNCTION sizing_get_size_charts()
  RETURNS SETOF size_chart AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test

  set search_path=zcat_api_r12_00_40;
  SELECT * FROM create_model_sku ('10K11A008');
*/
DECLARE
BEGIN
    RETURN QUERY
      SELECT
               sc_code::text,
               sc_description_message_key,
               ARRAY(
                  select row(
                           row(s_code, s_size_chart_code)::size_code,
                           s_supplier_size,
                           s_sort_key,
                           s_value
                         )::size
                    from zcat_commons.size
                   where s_size_chart_code = sc_code
               )
        FROM zcat_commons.size_chart;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
