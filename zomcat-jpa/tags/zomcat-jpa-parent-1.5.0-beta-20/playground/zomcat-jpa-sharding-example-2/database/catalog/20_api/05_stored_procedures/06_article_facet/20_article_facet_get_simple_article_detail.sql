CREATE OR REPLACE FUNCTION article_facet_get_simple_article_detail(p_simple_skus text[]) RETURNS SETOF salesforce_simple_article_detail AS
$BODY$

BEGIN
  RETURN QUERY
      SELECT asku.as_sku, ase.ase_ean::TEXT, b.b_name
        FROM zcat_data.article_sku asku
        JOIN zcat_data.article_model amodel ON amodel.am_model_sku_id = asku.as_model_id
        JOIN zcat_commons.brand b ON amodel.am_brand_code = b.b_code
        LEFT
        JOIN zcat_data.article_simple_ean ase ON asku.as_id = ase.ase_simple_sku_id
        WHERE asku.as_sku_type = 'SIMPLE' AND asku.as_sku = ANY(p_simple_skus);
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
