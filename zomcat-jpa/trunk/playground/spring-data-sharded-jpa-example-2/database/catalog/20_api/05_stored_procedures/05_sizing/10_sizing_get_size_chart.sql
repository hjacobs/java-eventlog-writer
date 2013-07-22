CREATE OR REPLACE FUNCTION sizing_get_size_chart(p_code text, p_with_sizes boolean default true)
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

   IF p_with_sizes = true THEN
       RETURN QUERY
         SELECT
                sc_code,
                sc_description_message_key,
                ARRAY(
                   select row(
                            row(s_code,s_size_chart_code)::size_code,
                            s_supplier_size,
                            s_sort_key,
                            s_value
                          )::size
                     from zcat_commons.size
                    where s_size_chart_code = sc_code
                )::size[]
           FROM zcat_commons.size_chart
          WHERE sc_code = p_code;
   ELSE
        RETURN QUERY
          SELECT
                 sc_code,
                 sc_description_message_key,
                 null::size[]
            FROM zcat_commons.size_chart
           WHERE sc_code = p_code;
   END IF;
END;
$BODY$
  LANGUAGE 'plpgsql'
  STABLE
  SECURITY DEFINER
  COST 100;
