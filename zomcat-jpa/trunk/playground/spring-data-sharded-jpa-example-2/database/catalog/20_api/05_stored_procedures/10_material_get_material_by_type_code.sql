create or replace function material_get_material_by_type_code(p_material_type_code option_value_type_code)
returns setof material as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Selects article material.
 *
 *
 * @ExpectedExecutionTime 10ms
 * @ExpectedExecutionFrequency Every admin article material request.
 */
/**  Test
  set search_path=zcat_api_r13_00_06;
  select * from article_get_material(null);
  select * from article_get_material('LEATHER');
*/
begin

  return query
    select m_code,
           CASE WHEN ov_code IS NULL THEN NULL::option_value_type_code ELSE
            ROW(
            'MATERIAL_TYPE',
            ov_code)::option_value_type_code END, --(select master_data_get_option_value_type_code_by_id(ov_id)),
           m_name_message_key
      from zcat_commons.material
 left join zcat_option_value.material_type on m_type_id = ov_id
     where (p_material_type_code is null) or (ov_code = p_material_type_code.code);

end
$BODY$
language plpgsql
    STABLE security definer
    cost 100;
