CREATE OR REPLACE FUNCTION article_facet_supplier_create_or_update_model(
    p_supplier_model  supplier_model,
    p_scope           flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a supplier model facet.
*/
DECLARE
    l_model_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_supplier_model.model_sku, 'MODEL')
      INTO l_model_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_model WHERE am_model_sku_id = l_model_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_supplier_model.model_sku USING ERRCODE = 'Z0005';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_supplier_model (
            afsm_model_sku_id,
            afsm_supplier_code,
            afsm_created_by,
            afsm_last_modified_by,
            afsm_flow_id,
            afsm_article_name,
            afsm_article_code,
            afsm_shoe_last_group
        )
        VALUES (
            l_model_sku_id,
            p_supplier_model.supplier_code,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_supplier_model.article_name,
            p_supplier_model.article_code,
            p_supplier_model.shoe_last_group);

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_supplier_model
           SET
               afsm_last_modified      = now(),
               afsm_last_modified_by   = p_scope.user_id,
               afsm_flow_id            = p_scope.flow_id,
               afsm_version            = p_supplier_model.version,

               afsm_article_name       = p_supplier_model.article_name,
               afsm_article_code       = p_supplier_model.article_code,
               afsm_shoe_last_group    = p_supplier_model.shoe_last_group
         WHERE afsm_model_sku_id = l_model_sku_id and afsm_supplier_code = p_supplier_model.supplier_code;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;