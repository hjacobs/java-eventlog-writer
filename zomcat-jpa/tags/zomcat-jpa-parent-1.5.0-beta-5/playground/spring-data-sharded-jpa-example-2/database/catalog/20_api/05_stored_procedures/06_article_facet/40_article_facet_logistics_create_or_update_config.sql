CREATE OR REPLACE FUNCTION article_facet_logistics_create_or_update_config(
    p_logistics_config  logistics_config,
    p_scope              flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

   Creates or updates a logistics config facet.
*/
DECLARE
    l_config_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_logistics_config.config_sku, 'CONFIG')
      INTO l_config_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_config WHERE ac_config_sku_id = l_config_sku_id) THEN
        RAISE EXCEPTION 'Article config with sku % not found.', p_logistics_config.config_sku USING ERRCODE = 'Z0003';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_logistics_config (
            aflc_config_sku_id,
            aflc_created_by,
            aflc_last_modified_by,
            aflc_flow_id,
            aflc_country_of_origin,
            aflc_customs_code,
            aflc_net_weight,
            aflc_gross_weight,
            aflc_net_volume,
            aflc_gross_volume,
            aflc_shipping_placement_id,
            aflc_is_cage_product,
            aflc_is_fragile,
            aflc_is_oversized_package,
            aflc_is_extra_heavy,
            aflc_is_hazardous,
            aflc_is_not_individually_packed,
            aflc_has_no_packaging,
            aflc_has_unsafe_packaging,
            aflc_is_ce_certified,
            aflc_article_set_size,
            aflc_is_hanging_garment,
            aflc_is_functional_test_required,
            aflc_is_perishable,
            aflc_is_comes_as_set
        )
        VALUES (
            l_config_sku_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_logistics_config.country_of_origin,
            p_logistics_config.customs_code,
            p_logistics_config.net_weight,
            p_logistics_config.gross_weight,
            p_logistics_config.net_volume,
            p_logistics_config.gross_volume,
            (select master_data_get_option_value_type_code_id(p_logistics_config.shipping_placement_type_code)),
            p_logistics_config.cage_product,
            p_logistics_config.fragile,
            p_logistics_config.oversized_package,
            p_logistics_config.extra_heavy,
            p_logistics_config.hazardous,
            p_logistics_config.not_individually_packed,
            p_logistics_config.no_packaging,
            p_logistics_config.unsafe_packaging,
            p_logistics_config.ce_certified,
            p_logistics_config.article_set_size,
            p_logistics_config.hanging_garment,
            p_logistics_config.functional_test_required,
            p_logistics_config.perishable,
            p_logistics_config.comes_as_set);

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_logistics_config
           SET
               aflc_last_modified                 = now(),
               aflc_last_modified_by              = p_scope.user_id,
               aflc_flow_id                       = p_scope.flow_id,
               aflc_version                       = p_logistics_config.version,

               aflc_country_of_origin             = p_logistics_config.country_of_origin,
               aflc_customs_code                  = p_logistics_config.customs_code,
               aflc_net_weight                    = p_logistics_config.net_weight,
               aflc_gross_weight                  = p_logistics_config.gross_weight,
               aflc_net_volume                    = p_logistics_config.net_volume,
               aflc_gross_volume                  = p_logistics_config.gross_volume,
               aflc_shipping_placement_id         = (select master_data_get_option_value_type_code_id(p_logistics_config.shipping_placement_type_code)),
               aflc_is_cage_product               = p_logistics_config.cage_product,
               aflc_is_fragile                    = p_logistics_config.fragile,
               aflc_is_oversized_package          = p_logistics_config.oversized_package,
               aflc_is_extra_heavy                = p_logistics_config.extra_heavy,
               aflc_is_hazardous                  = p_logistics_config.hazardous,
               aflc_is_not_individually_packed    = p_logistics_config.not_individually_packed,
               aflc_has_no_packaging              = p_logistics_config.no_packaging,
               aflc_has_unsafe_packaging          = p_logistics_config.unsafe_packaging,
               aflc_is_ce_certified               = p_logistics_config.ce_certified,
               aflc_article_set_size              = p_logistics_config.article_set_size,
               aflc_is_hanging_garment            = p_logistics_config.hanging_garment,
               aflc_is_functional_test_required   = p_logistics_config.functional_test_required,
               aflc_is_perishable                 = p_logistics_config.perishable,
               aflc_is_comes_as_set               = p_logistics_config.comes_as_set
        WHERE aflc_config_sku_id = l_config_sku_id;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;