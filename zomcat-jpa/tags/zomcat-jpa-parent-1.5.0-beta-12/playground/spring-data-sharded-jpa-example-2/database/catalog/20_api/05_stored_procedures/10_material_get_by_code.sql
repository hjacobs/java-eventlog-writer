CREATE OR REPLACE FUNCTION material_get_by_code (
  p_material_code  text
) RETURNS setof material AS
$$

  SELECT m_code,
         CASE WHEN material_type.ov_code IS NULL THEN NULL::option_value_type_code ELSE
          ROW(
          'MATERIAL_TYPE',
          material_type.ov_code
         )::option_value_type_code END, --(select master_data_get_option_value_type_code_by_id(m_type_id)),
         m_characteristic
     FROM zcat_commons.material
LEFT JOIN zcat_option_value.material_type material_type ON m_type_id = material_type.ov_id

   WHERE m_code = $1

$$
LANGUAGE SQL STABLE SECURITY DEFINER
COST 100;