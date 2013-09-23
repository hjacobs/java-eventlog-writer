CREATE OR REPLACE FUNCTION article_facet_sales_create_or_update_config(
    p_sales_config  sales_config,
    p_scope         flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a sales config facet.
*/
DECLARE
    l_config_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_sales_config.config_sku, 'CONFIG')
      INTO l_config_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_config WHERE ac_config_sku_id = l_config_sku_id) THEN
        RAISE EXCEPTION 'Article config with sku % not found.', p_sales_config.config_sku USING ERRCODE = 'Z0003';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_sales_config (
            afsc_config_sku_id,
            afsc_created_by,
            afsc_last_modified_by,
            afsc_flow_id,
            afsc_comment,
            afsc_sole_type_id,
            afsc_insole_type_id,
            afsc_trend1_id,
            afsc_trend2_id,
            afsc_textile_upper_id,
            afsc_shoe_upper_id,
            afsc_target_group_age_id,
            afsc_lining_type_id,
            afsc_shoe_lining_material_id,
            afsc_textile_lining_material_id,
            afsc_leather_type_id
        )
        VALUES (
            l_config_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_sales_config.comment,
            (select master_data_get_option_value_type_code_id(p_sales_config.sole_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.insole_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.trend1_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.trend2_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.textile_upper_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.shoe_upper_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.target_group_age_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.lining_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.shoe_lining_material_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.textile_lining_material_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_config.leather_type_code)));

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_sales_config
           SET
               afsc_last_modified                 = now(),
               afsc_last_modified_by              = p_scope.user_id,
               afsc_flow_id                       = p_scope.flow_id,
               afsc_version                       = p_sales_config.version,

               afsc_comment                       = p_sales_config.comment,
               afsc_sole_type_id                  = (select master_data_get_option_value_type_code_id(p_sales_config.sole_type_code)),
               afsc_insole_type_id                = (select master_data_get_option_value_type_code_id(p_sales_config.insole_type_code)),
               afsc_trend1_id                     = (select master_data_get_option_value_type_code_id(p_sales_config.trend1_type_code)),
               afsc_trend2_id                     = (select master_data_get_option_value_type_code_id(p_sales_config.trend2_type_code)),
               afsc_textile_upper_id              = (select master_data_get_option_value_type_code_id(p_sales_config.textile_upper_type_code)),
               afsc_shoe_upper_id                 = (select master_data_get_option_value_type_code_id(p_sales_config.shoe_upper_type_code)),
               afsc_target_group_age_id           = (select master_data_get_option_value_type_code_id(p_sales_config.target_group_age_type_code)),
               afsc_lining_type_id                = (select master_data_get_option_value_type_code_id(p_sales_config.lining_type_code)),
               afsc_shoe_lining_material_id       = (select master_data_get_option_value_type_code_id(p_sales_config.shoe_lining_material_type_code)),
               afsc_textile_lining_material_id    = (select master_data_get_option_value_type_code_id(p_sales_config.textile_lining_material_type_code)),
               afsc_leather_type_id               = (select master_data_get_option_value_type_code_id(p_sales_config.leather_type_code))
        WHERE afsc_config_sku_id = l_config_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;