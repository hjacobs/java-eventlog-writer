CREATE OR REPLACE FUNCTION price_conf_mgmt_persist_price_definition_changes (
    p_updated_price_definitions     updated_price_definition[],
    p_scope                         flow_scope
)
RETURNS SETOF price_definition AS

$BODY$
/* -- test
set search_path to zcat_api, public;
select zcat_api.price_conf_mgmt_persist_price_definition_changes(null::updated_price_definition[])

select zcat_api_r13_00_13.price_conf_mgmt_persist_price_definition_changes(
    ARRAY [((null,'BY121C00J',(1, 50, 'standard', FALSE, TRUE, TRUE), 100, null,'AF',null, '2015-04-21 18:44:24.000000 +02:00:00',null,null),'insert')]::updated_price_definition[]);


select zcat_api.price_conf_mgmt_persist_price_definition_changes(
    ARRAY [ ((null,'PVROj2-DTB4-7',null,0,null,null,'2013-02-21 18:44:24.000000 +01:00:00', '2013-04-21 18:44:24.000000 +02:00:00',null,null),'modify'),
        ((null,'PVROj2-DTB4-7',null,0,null,null,'2013-02-21 18:44:24.000000 +01:00:00', '2013-04-21 18:44:24.000000 +02:00:00',null,null),'modify')]::updated_price_definition[]);


select price_conf_mgmt_persist_price_definition_changes('{"(\"(1,nu767vkQJ,\"\"(1,50,standard,f,t,t)\"\",0,,FR,\"\"2012-12-21 18:53:24.000000 +01:00:00\"\",\"\"2013-04-21 18:53:24.000000 +02:00:00\"\",,)\",modified)","(\"(,nu767vkQJ,\"\"(1,50,standard,f,t,t)\"\",0,,FR,\"\"2013-02-19 18:53:24.000000 +01:00:00\"\",\"\"2013-02-21 18:53:24.000000 +01:00:00\"\",,)\",modified)"}'::updated_price_definition[]);


*/
DECLARE
    l_upd               updated_price_definition;
    l_price_definition  price_definition;
    l_pd_id             bigint;
    l_loop_pd_id        bigint;
    l_high_priority     boolean;
    l_inserted_pd_ids   bigint[];
    l_now               timestamp;
BEGIN

    l_inserted_pd_ids := ARRAY[]::bigint[];
    -- 'now' is fixed for the whole batch
    l_now = date_trunc('second', now());

    FOREACH l_upd IN ARRAY p_updated_price_definitions LOOP

        RAISE WARNING 'Process operation % for pd : %', l_upd.operation, l_upd.price_definition;

        l_price_definition := l_upd.price_definition;
        l_pd_id := l_price_definition.id;
        l_high_priority = l_upd.is_high_priority;

        IF l_price_definition.start_date IS NULL THEN
            l_price_definition.start_date := l_now;
        END IF;

        CASE l_upd.operation
        WHEN 'insert' THEN
            l_pd_id := price_conf_mgmt_create_or_update_price_definition(l_price_definition, p_scope, null, false, l_high_priority);
            IF l_pd_id IS NOT NULL THEN
                l_inserted_pd_ids := l_inserted_pd_ids || l_pd_id;
            END IF;

        WHEN 'modify_start' THEN
            UPDATE zcat_data.price_definition
               SET pd_start_date = l_price_definition.start_date
             WHERE pd_id = l_pd_id;

            UPDATE zcat_data.price_definition_additional_info
               SET pdai_last_modified_by = p_scope.user_id,
                   pdai_last_modified = l_now,
                   pdai_flow_id = p_scope.flow_id
             WHERE pdai_price_definition_id = l_pd_id;

            UPDATE zcat_data.price_definition
               SET pd_start_date = l_price_definition.start_date
              FROM zcat_data.price_definition_additional_info
             WHERE pd_id = pdai_price_definition_id
               AND pdai_original_price_definition_id = l_pd_id;

        WHEN 'modify_end' THEN
            UPDATE zcat_data.price_definition
               SET pd_end_date = l_price_definition.end_date
             WHERE pd_id = l_pd_id;

            UPDATE zcat_data.price_definition_additional_info
               SET pdai_last_modified_by = p_scope.user_id,
                   pdai_last_modified = l_now,
                   pdai_flow_id = p_scope.flow_id
             WHERE pdai_price_definition_id = l_pd_id;

            UPDATE zcat_data.price_definition
               SET pd_end_date = l_price_definition.end_date
              FROM zcat_data.price_definition_additional_info
             WHERE pd_id = pdai_price_definition_id
               AND pdai_original_price_definition_id = l_pd_id;

        WHEN 'delete' THEN
            perform price_conf_mgmt_delete_price_definition(l_pd_id);
        ELSE
            RAISE EXCEPTION 'Not supported operation % for pd: %', l_upd.operation, l_upd.price_definition;
        END CASE;
    END LOOP;

    -- get valid price_definitions for base_fallback
    RETURN QUERY
    select pd_id,
           as_sku,
           ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level,
           pd_price,
           pd_appdomain_id,
           pd_country_code,
           date_trunc('second', pd_start_date),
           date_trunc('second', pd_end_date),
           pd_partner_id,
           ROW (plrc_id, plrc_value)::price_level_reason_code
      from zcat_data.price_definition orig
      join zcat_data.article_sku on as_id = pd_sku_id
      join zcat_commons.price_level on pl_id = pd_price_level_id
      left join zcat_data.price_level_reason_code on plrc_id = pd_price_level_reason_code_id
     where pd_id = ANY(l_inserted_pd_ids)
       and pd_partner_id is null
       and pd_appdomain_id is null
       and pd_price_level_id = 1;

END

$BODY$
LANGUAGE plpgsql
    VOLATILE SECURITY DEFINER
    COST 100;
