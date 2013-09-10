CREATE OR REPLACE FUNCTION article_facet_accounting_create_or_update_model(
    p_accounting_model  accounting_model,
    p_scope             flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a accounting model facet.

*/
DECLARE
    l_model_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_accounting_model.model_sku, 'MODEL')
      INTO l_model_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_model WHERE am_model_sku_id = l_model_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_accounting_model.model_sku USING ERRCODE = 'Z0005';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_accounting_model (
            afam_model_sku_id,
            afam_created_by,
            afam_last_modified_by,
            afam_flow_id,
            afam_value_added_tax_classification_id,
            afam_input_tax_classification_id
        )
        VALUES (
            l_model_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            (select master_data_get_option_value_type_code_id(p_accounting_model.value_added_tax_classification_type_code)),
            (select master_data_get_option_value_type_code_id(p_accounting_model.input_tax_classification_type_code)));

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_accounting_model
           SET
               afam_last_modified                      = now(),
               afam_last_modified_by                   = p_scope.user_id,
               afam_flow_id                            = p_scope.flow_id,
               afam_version                            = p_accounting_model.version,

               afam_value_added_tax_classification_id  = (select master_data_get_option_value_type_code_id(p_accounting_model.value_added_tax_classification_type_code)),
               afam_input_tax_classification_id        = (select master_data_get_option_value_type_code_id(p_accounting_model.input_tax_classification_type_code))
        WHERE afam_model_sku_id = l_model_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;