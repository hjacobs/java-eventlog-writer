CREATE OR REPLACE FUNCTION sizing_get_dimension_group_id(
  p_dimension_codes text[],
  p_ignore_order boolean default false
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
  l_group_id int;
BEGIN
--  RAISE INFO 'SIZE_CHART get dimension group id for %', p_dimension_codes;

  SELECT sdgb_group_id
    INTO l_group_id
    FROM (
           select sdgb_group_id, count(*) group_size
             from (
                    select sdgb_group_id group_id
                      from (
                            select row_number() over() as position, *
                              from unnest(p_dimension_codes) code
                           ) sq
                      left
                      join zcat_commons.size_dimension_group_binding
                        on sdgb_code = code
                       and (
                             sdgb_position = position
                             or
                             p_ignore_order
                           )
                     group
                        by sdgb_group_id
                    having count(sdgb_code) = array_length(p_dimension_codes, 1)
                  ) sq2
             join zcat_commons.size_dimension_group_binding
               on sdgb_group_id = group_id
            group
               by sdgb_group_id
         ) sq3
   WHERE group_size = array_length(p_dimension_codes, 1);

  RETURN l_group_id;
END;
$BODY$
  LANGUAGE 'plpgsql' STABLE SECURITY DEFINER
  COST 100;
