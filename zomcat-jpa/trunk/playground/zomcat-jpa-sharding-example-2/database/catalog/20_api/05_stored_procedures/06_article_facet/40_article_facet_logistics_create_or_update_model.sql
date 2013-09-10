CREATE OR REPLACE FUNCTION article_facet_logistics_create_or_update_model(
    p_logistics_model  logistics_model,
    p_scope            flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a logistics model facet.
*/
DECLARE
    l_model_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_logistics_model.model_sku, 'MODEL')
      INTO l_model_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_model WHERE am_model_sku_id = l_model_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_logistics_model.model_sku USING ERRCODE = 'Z0005';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_logistics_model (
            aflm_model_sku_id,
            aflm_created_by,
            aflm_last_modified_by,
            aflm_flow_id,
            aflm_country_of_origin,
            aflm_bootleg_type_id,
            aflm_customs_code,
            aflm_alcohol_strength
        )
        VALUES (
            l_model_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_logistics_model.country_of_origin,
            (select master_data_get_option_value_type_code_id(p_logistics_model.bootleg_type_code)),
            p_logistics_model.customs_code,
            p_logistics_model.alcohol_strength);

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_logistics_model
           SET
               aflm_last_modified       = now(),
               aflm_last_modified_by    = p_scope.user_id,
               aflm_flow_id             = p_scope.flow_id,
               aflm_version             = p_logistics_model.version,

               aflm_country_of_origin   = p_logistics_model.country_of_origin,
               aflm_bootleg_type_id     = (select master_data_get_option_value_type_code_id(p_logistics_model.bootleg_type_code)),
               aflm_customs_code        = p_logistics_model.customs_code,
               aflm_alcohol_strength    = p_logistics_model.alcohol_strength
         WHERE aflm_model_sku_id = l_model_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;