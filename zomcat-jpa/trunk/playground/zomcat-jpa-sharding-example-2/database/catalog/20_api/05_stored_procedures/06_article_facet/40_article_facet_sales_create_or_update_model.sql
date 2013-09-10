CREATE OR REPLACE FUNCTION article_facet_sales_create_or_update_model(
    p_sales_model  sales_model,
    p_scope        flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a sales model facet.
*/
DECLARE
    l_model_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_sales_model.model_sku, 'MODEL')
      INTO l_model_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_model WHERE am_model_sku_id = l_model_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_sales_model.model_sku USING ERRCODE = 'Z0005';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_sales_model (
            afsm_model_sku_id,
            afsm_created_by,
            afsm_last_modified_by,
            afsm_flow_id,
            afsm_fitting_id,
            afsm_closure_id,
            afsm_toe_cap_id,
            afsm_sleeve_type_id,
            afsm_heel_height,
            afsm_heel_type_id,
            afsm_leg_type_id,
            afsm_neck_line_id,
            afsm_shoe_upper_id,
            afsm_textile_membrane_id,
            afsm_fit_type_id,
            afsm_is_extra_large,
            afsm_sport_type_id,
            afsm_sub_sport_type_id
        )
        VALUES (
            l_model_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            (select master_data_get_option_value_type_code_id(p_sales_model.fitting_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.closure_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.toe_cap_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.sleeve_type_code)),
            p_sales_model.heel_height,
            (select master_data_get_option_value_type_code_id(p_sales_model.heel_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.leg_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.neck_line_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.shoe_upper_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.textile_membrane_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.fit_type_code)),
            p_sales_model.extra_large,
            (select master_data_get_option_value_type_code_id(p_sales_model.sport_type_code)),
            (select master_data_get_option_value_type_code_id(p_sales_model.sub_sport_type_code)));

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_sales_model
           SET
               afsm_last_modified          = now(),
               afsm_last_modified_by       = p_scope.user_id,
               afsm_flow_id                = p_scope.flow_id,
               afsm_version                = p_sales_model.version,

               afsm_fitting_id             = (select master_data_get_option_value_type_code_id(p_sales_model.fitting_type_code)),
               afsm_closure_id             = (select master_data_get_option_value_type_code_id(p_sales_model.closure_type_code)),
               afsm_toe_cap_id             = (select master_data_get_option_value_type_code_id(p_sales_model.toe_cap_type_code)),
               afsm_sleeve_type_id         = (select master_data_get_option_value_type_code_id(p_sales_model.sleeve_type_code)),
               afsm_heel_height            = p_sales_model.heel_height,
               afsm_heel_type_id           = (select master_data_get_option_value_type_code_id(p_sales_model.heel_type_code)),
               afsm_leg_type_id            = (select master_data_get_option_value_type_code_id(p_sales_model.leg_type_code)),
               afsm_neck_line_id           = (select master_data_get_option_value_type_code_id(p_sales_model.neck_line_type_code)),
               afsm_shoe_upper_id          = (select master_data_get_option_value_type_code_id(p_sales_model.shoe_upper_type_code)),
               afsm_textile_membrane_id    = (select master_data_get_option_value_type_code_id(p_sales_model.textile_membrane_type_code)),
               afsm_fit_type_id            = (select master_data_get_option_value_type_code_id(p_sales_model.fit_type_code)),
               afsm_is_extra_large         = p_sales_model.extra_large,
               afsm_sport_type_id          = (select master_data_get_option_value_type_code_id(p_sales_model.sport_type_code)),
               afsm_sub_sport_type_id      = (select master_data_get_option_value_type_code_id(p_sales_model.sub_sport_type_code))
        WHERE afsm_model_sku_id = l_model_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;