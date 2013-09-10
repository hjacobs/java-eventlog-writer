CREATE OR REPLACE function article_get_ean_mapping(
    p_eans text[],

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
 * if the ean currently the active one.
 *
 *
 * @ExpectedExecutionTime 10ms
 * @ExpectedExecutionFrequency Every time the WS gets called
 */
/**  Test
  set search_path=zcat_api_r13_00_06;
  select * from article_get_ean_mapping(null)
  select * from article_get_ean_mapping('{2109221891928, 2798498446124}')
*/
BEGIN

  RETURN QUERY
    SELECT ase_ean::text, as_sku, ase_is_active
      FROM zcat_data.article_sku
      JOIN zcat_data.article_simple_ean ON ase_simple_sku_id = as_id
     WHERE ase_ean = ANY(p_eans::ean13[]);

END
$BODY$
LANGUAGE plpgsql
    VOLATILE SECURITY DEFINER
    COST 100;
