CREATE OR REPLACE FUNCTION article_facet_production_create_or_update_model(
    p_production_model  production_model,
    p_scope             flow_scope
) RETURNS text AS
$BODY$
/*
  $Id$
  $HeadURL$

  Creates or updates a production model facet.
*/
DECLARE
    l_model_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_production_model.model_sku, 'MODEL')
      INTO l_model_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_model WHERE am_model_sku_id = l_model_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_production_model.model_sku USING ERRCODE = 'Z0005';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_production_model (
            afpm_model_sku_id,
            afpm_created_by,
            afpm_last_modified_by,
            afpm_flow_id,
            afpm_quality_group_q,
            afpm_type_q_id,
            afpm_material_weight,
            afpm_mesh,
            afpm_material_detail_id
        )
        VALUES (
            l_model_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_production_model.quality_group_q,
            (select master_data_get_option_value_type_code_id(p_production_model.type_code_q)),
            p_production_model.material_weight,
            p_production_model.mesh,
            (select master_data_get_option_value_type_code_id(p_production_model.material_detail_type_code))
        );

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_production_model
           SET
               afpm_last_modified       = now(),
               afpm_last_modified_by    = p_scope.user_id,
               afpm_flow_id             = p_scope.flow_id,
               afpm_version             = p_production_model.version,

               afpm_quality_group_q     = p_production_model.quality_group_q,
               afpm_type_q_id           = (select master_data_get_option_value_type_code_id(p_production_model.type_code_q)),
               afpm_material_weight     = p_production_model.material_weight,
               afpm_mesh                = p_production_model.mesh,
               afpm_material_detail_id  = (select master_data_get_option_value_type_code_id(p_production_model.material_detail_type_code))
         WHERE afpm_model_sku_id = l_model_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;