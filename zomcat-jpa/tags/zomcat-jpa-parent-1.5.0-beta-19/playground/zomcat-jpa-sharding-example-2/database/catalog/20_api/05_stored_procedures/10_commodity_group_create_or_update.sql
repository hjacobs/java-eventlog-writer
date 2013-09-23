create or replace function commodity_group_create_or_update(
    p_commodity_group commodity_group,
    p_scope           flow_scope)
returns void as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Update or create a commodity group article_create_or_update_commodity_group
 *
 * @ExpectedExecutionTime 20ms
 * @ExpectedExecutionFrequency not that often
 */
/* --testing

    begin;
    -- set search_path to zcat_api_r13_00_06, public;

select article_create_or_update_commodity_group(
    ROW(('code', 'name', '3000','1234')::commodity_group, ('user_id','flow_id')::flow_scope);

select article_create_or_update_commodity_group(
    ROW(('code', 'name', '3000','1234')::commodity_group, ('user_id','flow_id')::flow_scope);

    rollback;
*/
DECLARE
BEGIN
    BEGIN
       -- create the record:
       INSERT INTO zcat_commons.commodity_group(
          cg_code,
          cg_parent_code,
          cg_name_message_key,
          cg_dd_sub_product_group,
          cg_is_active,
          cg_created_by,
          cg_last_modified_by,
          cg_flow_id)
        VALUES (
          p_commodity_group.commodity_group_code,
          p_commodity_group.parent_Commodity_group_code,
          p_commodity_group.name_message_key,
          p_commodity_group.dd_sub_product_group,
          p_commodity_group.active,
          p_scope.user_id,
          p_scope.user_id,
          p_scope.flow_id);

       EXCEPTION
           WHEN unique_violation THEN
             -- seems as if we need to update the existing record:
              UPDATE zcat_commons.commodity_group
                 SET cg_parent_code             = p_commodity_group.parent_Commodity_group_code,
                     cg_name_message_key        = p_commodity_group.name_message_key,
                     cg_dd_sub_product_group    = p_commodity_group.dd_sub_product_group,
                     cg_is_active               = p_commodity_group.active,
                     cg_last_modified           = now(),
                     cg_last_modified_by        = p_scope.user_id,
                     cg_flow_id               = p_scope.flow_id
               WHERE cg_code = p_commodity_group.commodity_group_code;
    END;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
