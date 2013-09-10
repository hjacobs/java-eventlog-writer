CREATE OR REPLACE FUNCTION article_facet_logistics_create_or_update_simple(
    p_logistics_simple  logistics_simple,
    p_scope             flow_scope
) RETURNS text AS
$BODY$
/*
  $Id$
  $HeadURL$

  Creates or updates a logistics simple facet.
*/
DECLARE
    l_simple_sku_id int;
BEGIN
    -- determine sku id
    SELECT article_get_sku_id(p_logistics_simple.simple_sku, 'SIMPLE')
      INTO l_simple_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_simple WHERE as_simple_sku_id = l_simple_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_logistics_simple.simple_sku USING ERRCODE = 'Z0004';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_logistics_simple (
            afls_simple_sku_id,
            afls_created_by,
            afls_last_modified_by,
            afls_flow_id,
            afls_country_of_origin,
            afls_customs_code,
            afls_net_weight,
            afls_gross_weight,
            afls_net_volume,
            afls_gross_volume,
            afls_shipping_placement_id,
            afls_package_height,
            afls_package_width,
            afls_package_length,
            afls_is_customs_notification_required,
            afls_has_preferential_treatment
        )
        VALUES (
            l_simple_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_logistics_simple.country_of_origin,
            p_logistics_simple.customs_code,
            p_logistics_simple.net_weight,
            p_logistics_simple.gross_weight,
            p_logistics_simple.net_volume,
            p_logistics_simple.gross_volume,
            (select master_data_get_option_value_type_code_id(p_logistics_simple.shipping_placement_type_code)),
            p_logistics_simple.package_height,
            p_logistics_simple.package_width,
            p_logistics_simple.package_length,
            p_logistics_simple.customs_notification_required,
            -- remove when all clients are able to set this field
            COALESCE(p_logistics_simple.preferential_treatment, false));

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_logistics_simple
           SET
               afls_last_modified                    = now(),
               afls_last_modified_by                 = p_scope.user_id,
               afls_flow_id                          = p_scope.flow_id,
               afls_version                          = p_logistics_simple.version,

               afls_country_of_origin                = p_logistics_simple.country_of_origin,
               afls_customs_code                     = p_logistics_simple.customs_code,
               afls_net_weight                       = p_logistics_simple.net_weight,
               afls_gross_weight                     = p_logistics_simple.gross_weight,
               afls_net_volume                       = p_logistics_simple.net_volume,
               afls_gross_volume                     = p_logistics_simple.gross_volume,
               afls_shipping_placement_id            = (select master_data_get_option_value_type_code_id(p_logistics_simple.shipping_placement_type_code)),
               afls_package_height                   = p_logistics_simple.package_height,
               afls_package_width                    = p_logistics_simple.package_width,
               afls_package_length                   = p_logistics_simple.package_length,
               afls_is_customs_notification_required = p_logistics_simple.customs_notification_required,
               -- remove when all clients are able to set this field
               afls_has_preferential_treatment       = COALESCE(p_logistics_simple.preferential_treatment, false)
         WHERE afls_simple_sku_id = l_simple_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;