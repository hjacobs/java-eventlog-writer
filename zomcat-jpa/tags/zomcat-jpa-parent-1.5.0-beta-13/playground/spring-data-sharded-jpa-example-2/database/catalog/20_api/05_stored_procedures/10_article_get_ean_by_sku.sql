CREATE OR REPLACE FUNCTION article_get_ean_by_sku(
  p_sku text
  )
  RETURNS text AS
$BODY$
-- $Id$
-- $HeadURL$
/**
 * Get the EAN associated with this simple SKU
 *
 * @ExpectedExecutionTime 50 ms
 * @ExpectedExecutionFrequency Daily
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from article_get_ean_by_sku('TI116X009-7020360000');
 rollback;

 */

SELECT ase_ean::text
  FROM zcat_data.article_sku
  JOIN zcat_data.article_simple_ean ON ase_simple_sku_id = as_id
 WHERE as_sku = $1
   AND ase_is_active
 LIMIT 1;

$BODY$
  LANGUAGE 'sql' VOLATILE SECURITY DEFINER
  COST 100;