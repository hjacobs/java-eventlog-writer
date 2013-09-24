CREATE OR REPLACE FUNCTION sizing_get_size_dimension(p_code text)
  RETURNS SETOF size_dimension AS
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
             sd_code,
             sd_name,
             sd_display_message_key
        FROM zcat_commons.size_dimension
       WHERE sd_code = p_code;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
