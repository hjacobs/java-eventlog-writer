CREATE OR REPLACE FUNCTION article_facet_supplier_create_or_update_simple(
    p_supplier_simple supplier_simple,
    p_scope           flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a supplier simple facet.
*/
DECLARE
    l_simple_sku_id int;
BEGIN

    -- determine sku id
    SELECT article_get_sku_id(p_supplier_simple.simple_sku, 'SIMPLE')
      INTO l_simple_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_simple WHERE as_simple_sku_id = l_simple_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_supplier_simple.simple_sku USING ERRCODE = 'Z0004';
    END IF;

    BEGIN

        INSERT INTO zcat_data.article_facet_supplier_simple (
            afss_simple_sku_id,
            afss_supplier_code,
            afss_created_by,
            afss_last_modified_by,
            afss_flow_id,
            afss_article_code
        )
        VALUES (
            l_simple_sku_id,
            p_supplier_simple.supplier_code,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_supplier_simple.article_code);

        RETURN 'CREATE';

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_facet_supplier_simple
           SET
               afss_last_modified       = now(),
               afss_last_modified_by    = p_scope.user_id,
               afss_flow_id             = p_scope.flow_id,
               afss_version             = p_supplier_simple.version,

               afss_article_code        = p_supplier_simple.article_code
        WHERE afss_simple_sku_id = l_simple_sku_id and afss_supplier_code = p_supplier_simple.supplier_code;

        RETURN 'UPDATE';

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;