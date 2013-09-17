CREATE OR REPLACE FUNCTION article_facet_production_create_or_update_config(
    p_production_config  production_config,
    p_scope              flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a production config facet.
*/
DECLARE
    l_config_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_production_config.config_sku, 'CONFIG')
      INTO l_config_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_config WHERE ac_config_sku_id = l_config_sku_id) THEN
        RAISE EXCEPTION 'Article config with sku % not found.', p_production_config.config_sku USING ERRCODE = 'Z0003';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_production_config (
            afpc_config_sku_id,
            afpc_created_by,
            afpc_last_modified_by,
            afpc_flow_id,
            afpc_lead_time_days,
            afpc_material_id
        )
        VALUES (
            l_config_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_production_config.lead_time,
            (select master_data_get_option_value_type_code_id(p_production_config.production_material_type_code)));

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_production_config
           SET
               afpc_last_modified        = now(),
               afpc_last_modified_by     = p_scope.user_id,
               afpc_flow_id              = p_scope.flow_id,
               afpc_version              = p_production_config.version,

               afpc_lead_time_days       = p_production_config.lead_time,
               afpc_material_id         = (select master_data_get_option_value_type_code_id(p_production_config.production_material_type_code))
         WHERE afpc_config_sku_id = l_config_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;