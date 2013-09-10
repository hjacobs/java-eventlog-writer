CREATE OR REPLACE FUNCTION article_facet_logistics_create_or_update_package_information(
    p_package_informations  package_information[],
    p_scope                 flow_scope
) RETURNS SETOF package_information_response AS
$BODY$


/*
-- test
show search_path;
set search_path to zcat_api_r13_00_20, public;

SELECT article_facet_logistics_create_or_update_package_information(
    ARRAY[ROW('XC621C04F-2060000001', 1, 2, 3, 4, 5)::package_information],
    ROW('', '')::flow_scope
);
*/

BEGIN

    RAISE INFO 'received %', p_package_informations;

    RETURN QUERY
    WITH
    found_sku AS (
      SELECT pp.*,              -- will contain unnested info from p_package_informations
             as_id,             -- will be NULL if not found in zcat_data.article_sku
             as_simple_sku_id   -- will be NULL if not found in zcat_data.article_simple
        FROM unnest(p_package_informations) as pp
        LEFT
        JOIN zcat_data.article_sku ON as_sku = pp.simple_sku AND as_sku_type = 'SIMPLE'
        LEFT
        JOIN zcat_data.article_simple ON as_simple_sku_id = as_id
    ),
    to_insert AS (
        SELECT p_scope.user_id as user_id,
               p_scope.flow_id as flow_id,
               found_sku.as_simple_sku_id as simple_sku_id,
               gross_weight,
               gross_volume,
               package_height,
               package_width,
               package_length,
               0 as "version"
          FROM found_sku
          LEFT
          JOIN zcat_data.article_facet_logistics_simple ON afls_simple_sku_id = found_sku.as_simple_sku_id
         WHERE afls_simple_sku_id IS NULL
           AND found_sku.as_simple_sku_id IS NOT NULL
           AND (gross_weight IS NOT NULL
            OR  gross_volume IS NOT NULL
            OR  package_height IS NOT NULL
            OR  package_width IS NOT NULL
            OR  package_length IS NOT NULL)
    ),
    insert_stmt AS (
        INSERT INTO zcat_data.article_facet_logistics_simple(
            afls_simple_sku_id, afls_created_by, afls_last_modified_by, afls_flow_id,
            afls_gross_weight, afls_gross_volume, afls_package_height,
            afls_package_width, afls_package_length, afls_version)
        SELECT simple_sku_id, user_id, user_id, flow_id,
               gross_weight, gross_volume, package_height,
               package_width, package_length, "version"
          FROM to_insert
        RETURNING afls_simple_sku_id -- will be returned as NOT NULL
    ),
    update_stmt AS (
      UPDATE zcat_data.article_facet_logistics_simple
         SET afls_gross_weight = COALESCE(found_sku.gross_weight, afls_gross_weight),
             afls_gross_volume = COALESCE(found_sku.gross_volume, afls_gross_volume),
             afls_package_height = COALESCE(found_sku.package_height, afls_package_height),
             afls_package_width =  COALESCE(found_sku.package_width, afls_package_width),
             afls_package_length = COALESCE(found_sku.package_length, afls_package_length)
        FROM found_sku
       WHERE afls_simple_sku_id = found_sku.as_simple_sku_id
      RETURNING afls_simple_sku_id -- will be returned as NOT NULL only if existed in zcat_data.article_facet_logistics_simple
    )
    SELECT simple_sku,
           CASE WHEN as_id IS NULL OR as_simple_sku_id IS NULL OR COALESCE(ins.afls_simple_sku_id, upd.afls_simple_sku_id) IS NULL
                THEN 'ERROR' ELSE 'SUCCESS' END AS response_code,
           CASE WHEN as_id IS NULL THEN 'simple sku not found on table article_sku'
                WHEN as_simple_sku_id IS NULL THEN 'simple sku not found on table article_simple'
                WHEN COALESCE(ins.afls_simple_sku_id, upd.afls_simple_sku_id) IS NULL THEN 'error on create or update table article_facet_logistics_simple'
           END as response_message
      FROM found_sku
      LEFT
      JOIN update_stmt upd ON as_id IS NOT DISTINCT FROM upd.afls_simple_sku_id
      LEFT
      JOIN insert_stmt ins ON as_id IS NOT DISTINCT FROM ins.afls_simple_sku_id;

END;
$BODY$

LANGUAGE plpgsql
  VOLATILE
  SECURITY DEFINER
  COST 100;