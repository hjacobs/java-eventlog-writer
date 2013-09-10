CREATE OR REPLACE FUNCTION article_facet_supplier_create_or_update_config(
    p_supplier_config  supplier_config,
    p_scope            flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a supplier config facet.
*/
DECLARE
    l_config_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_supplier_config.config_sku, 'CONFIG')
      INTO l_config_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_config WHERE ac_config_sku_id = l_config_sku_id) THEN
        RAISE EXCEPTION 'Article config with sku % not found.', p_supplier_config.config_sku USING ERRCODE = 'Z0003';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_supplier_config (
            afsc_config_sku_id,
            afsc_supplier_code,
            afsc_created_by,
            afsc_last_modified_by,
            afsc_flow_id,
            afsc_article_code,
            afsc_color_code,
            afsc_color_description,
            afsc_availability_id,
            afsc_upper_material_description,
            afsc_lining_description,
            afsc_sole_description,
            afsc_inner_sole_description
        )
        VALUES (
            l_config_sku_id,
            p_supplier_config.supplier_code,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_supplier_config.article_code,
            p_supplier_config.color_code,
            p_supplier_config.color_description,
            (select master_data_get_option_value_type_code_id(p_supplier_config.availability_type_code)),
            p_supplier_config.upper_material_description,
            p_supplier_config.lining_description,
            p_supplier_config.sole_description,
            p_supplier_config.inner_sole_description);

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_supplier_config
           SET
               afsc_last_modified              = now(),
               afsc_last_modified_by           = p_scope.user_id,
               afsc_flow_id                    = p_scope.flow_id,
               afsc_version                    = p_supplier_config.version,

               afsc_article_code               = p_supplier_config.article_code,
               afsc_color_code                 = p_supplier_config.color_code,
               afsc_color_description          = p_supplier_config.color_description,
               afsc_availability_id            = (select master_data_get_option_value_type_code_id(p_supplier_config.availability_type_code)),
               afsc_upper_material_description = p_supplier_config.upper_material_description,
               afsc_lining_description         = p_supplier_config.lining_description,
               afsc_sole_description           = p_supplier_config.sole_description,
               afsc_inner_sole_description     = p_supplier_config.inner_sole_description
         WHERE afsc_config_sku_id = l_config_sku_id AND afsc_supplier_code = p_supplier_config.supplier_code;

        RETURN 'UPDATE';
    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;