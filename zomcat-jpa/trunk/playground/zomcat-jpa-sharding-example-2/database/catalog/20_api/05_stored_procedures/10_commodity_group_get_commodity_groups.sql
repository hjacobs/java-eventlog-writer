create or replace function commodity_group_get_commodity_groups()
returns setof commodity_group as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Selects all commodity groups.
 *
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency cached - not that much.
 */
/**  Test
  set search_path=zcat_api_r13_00_06;
  select * from article_get_all_commodity_groups();
*/
begin
  return query
    select  cg_code,
            cg_parent_code,
            cg_name_message_key,
            cg_dd_sub_product_group,
            cg_is_active,
            null::text[]
      from zcat_commons.commodity_group;
end
$BODY$
language plpgsql
    STABLE security definer
    cost 100;
