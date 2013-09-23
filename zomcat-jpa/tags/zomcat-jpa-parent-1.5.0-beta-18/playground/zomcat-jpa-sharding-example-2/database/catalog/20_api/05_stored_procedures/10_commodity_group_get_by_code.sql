CREATE OR REPLACE FUNCTION commodity_group_get_by_code (
  p_commodity_group_code  text
) RETURNS setof commodity_group AS
$$

  SELECT cg_code,
         cg_parent_code,
         cg_name_message_key,
         cg_dd_sub_product_group,
         cg_is_active,
         ARRAY(select child.cg_code from zcat_commons.commodity_group as child where child.cg_parent_code = $1)
    FROM zcat_commons.commodity_group
   WHERE cg_code = $1;

$$
LANGUAGE SQL STABLE SECURITY DEFINER
COST 100;