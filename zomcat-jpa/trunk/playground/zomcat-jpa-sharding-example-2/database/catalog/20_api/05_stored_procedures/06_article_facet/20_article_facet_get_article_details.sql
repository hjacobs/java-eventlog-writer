CREATE OR REPLACE FUNCTION article_facet_get_article_details(p_config_sku text) RETURNS SETOF salesforce_article_detail AS
$BODY$
-- $Id$
-- $HeadURL$
/**
 * Get config details for salesforce.
 *
 * @ExpectedExecutionTime 50 ms
 * @ExpectedExecutionFrequency Hourly
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from article_facet_get_article_details('abc');
 rollback;

 */
BEGIN
  RETURN QUERY
         SELECT DISTINCT ON(ase_ean)
                t_skus.as_sku,
                ass_size_code,
                s_supplier_size,
                ase_ean::text
           FROM zcat_data.article_sku config_sku
           JOIN zcat_data.article_sku t_skus ON config_sku.as_id IN (t_skus.as_config_id, t_skus.as_id)
           LEFT JOIN zcat_data.article_simple simple ON t_skus.as_id = simple.as_simple_sku_id
           LEFT JOIN zcat_data.article_simple_size ON simple.as_simple_sku_id = ass_article_simple_sku_id
           JOIN zcat_commons.size
             ON ass_size_chart_code = s_size_chart_code
            AND ass_size_code = s_code
           JOIN zcat_commons.size_chart ON s_size_chart_code = sc_code
           JOIN zcat_commons.size_dimension ON sc_dimension_code = sd_code
           JOIN zcat_commons.size_dimension_group_binding
             ON sdgb_code = sd_code
            AND sdgb_position = 1 --> size_code
           LEFT
           JOIN zcat_data.article_facet_supplier_simple ON afss_simple_sku_id = simple.as_simple_sku_id
           JOIN zcat_data.article_simple_ean ON ase_simple_sku_id = simple.as_simple_sku_id
          WHERE config_sku.as_sku = p_config_sku
            AND config_sku.as_sku_type = 'CONFIG';
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;