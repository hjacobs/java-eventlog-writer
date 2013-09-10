CREATE OR REPLACE FUNCTION sizing_get_size_charts_without_sizes()
  RETURNS SETOF size_chart AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test

  set search_path=zcat_api_r12_00_40;
  SELECT * FROM sizing_get_size_charts_without_sizes();
*/
DECLARE
BEGIN
    RETURN QUERY
      SELECT
               sc_code::text,
               sc_description_message_key,
               null::size[]
        FROM zcat_commons.size_chart;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
