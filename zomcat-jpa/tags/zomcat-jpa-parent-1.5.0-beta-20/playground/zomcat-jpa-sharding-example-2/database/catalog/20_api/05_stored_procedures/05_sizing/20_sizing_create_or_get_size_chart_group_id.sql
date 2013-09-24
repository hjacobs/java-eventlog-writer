CREATE OR REPLACE FUNCTION sizing_create_or_get_size_chart_group_id(
  p_size_chart_codes text[],
  p_scope       flow_scope
)
  RETURNS int AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test

  set search_path=zcat_api_r12_00_40;
  SELECT * FROM create_model_sku ('10K11A008');
*/
DECLARE
  l_dimension_group_id int;
  l_dimension_codes text[];
  l_chart_group_id int;
BEGIN

  l_dimension_codes := ARRAY(SELECT substr(code, 9,2) FROM unnest(p_size_chart_codes) code);
  l_dimension_group_id := sizing_get_dimension_group_id(l_dimension_codes);

  IF l_dimension_group_id IS NULL THEN
    RAISE EXCEPTION 'invalid dimension code combination %', p_size_chart_codes;
  END IF;

  SELECT scgb_size_chart_group_id
    INTO l_chart_group_id
    FROM (
            select scgb_size_chart_group_id, array_agg(scgb_size_chart_code order by scgb_size_chart_code) chart_codes
              from zcat_commons.size_chart_group_binding
             group
                by scgb_size_chart_group_id
         ) sq
   WHERE chart_codes = ARRAY(
           select code
             from unnest(p_size_chart_codes) code
            order
               by code
         );


  IF l_chart_group_id IS NULL THEN
    l_chart_group_id := nextval('zcat_commons.size_chart_group_scg_id_seq');

    INSERT
      INTO zcat_commons.size_chart_group (
             scg_id,
             scg_dimension_group_id,
             scg_created_by,
             scg_last_modified_by,
             scg_flow_id
           )
    VALUES (
             l_chart_group_id,
             l_dimension_group_id,
             p_scope.user_id,
             p_scope.user_id,
             p_scope.flow_id
           );

    INSERT
      INTO zcat_commons.size_chart_group_binding (
             scgb_size_chart_group_id,
             scgb_size_chart_code,
             scgb_created_by,
             scgb_last_modified_by,
             scgb_flow_id
           )
             select l_chart_group_id,
                    chart_code,
                    p_scope.user_id,
                    p_scope.user_id,
                    p_scope.flow_id
               from unnest(p_size_chart_codes) chart_code;

  END IF;

  RETURN l_chart_group_id;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
