CREATE OR REPLACE function article_get_ean_mapping_by_sku(
    p_skus text[],

    OUT ean text,
    OUT sku text,
    OUT is_active boolean
)RETURNS SETOF RECORD AS
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * For all passed eans an ean-sku mapping will be returned together with the is_active flag to show
 * if the ean is currently active.
 *
 * @ExpectedExecutionTime 10ms
 * @ExpectedExecutionFrequency Every time the WS gets called
 */
/**  Test
  select * from article_get_ean_mapping_by_sku(null)
  select * from article_get_ean_mapping_by_sku('{abc, def}')
*/
BEGIN

  RETURN QUERY
    SELECT ase_ean::text, as_sku, ase_is_active
      FROM zcat_data.article_sku
      JOIN zcat_data.article_simple_ean ON ase_simple_sku_id = as_id
     WHERE as_sku = ANY(p_skus)
       AND as_sku_type = 'SIMPLE';

END
$BODY$
LANGUAGE plpgsql
    VOLATILE SECURITY DEFINER
    COST 100;
