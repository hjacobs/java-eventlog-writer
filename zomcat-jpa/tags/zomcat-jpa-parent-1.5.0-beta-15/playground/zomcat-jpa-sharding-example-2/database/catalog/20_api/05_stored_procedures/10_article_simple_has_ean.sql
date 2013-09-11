CREATE OR REPLACE FUNCTION article_simple_has_ean(
  p_sku text
  )
  RETURNS boolean AS
$BODY$
-- $Id$
-- $HeadURL$
/**
 * Does the simple have an EAN?
 *
 * @ExpectedExecutionTime 50 ms
 * @ExpectedExecutionFrequency Hourly
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from article_simple_has_ean('TI116X009-7020360000');
 rollback;

 */

SELECT EXISTS(
    SELECT 1
      FROM zcat_data.article_sku
      JOIN zcat_data.article_simple_ean ON ase_simple_sku_id = as_id
     WHERE as_sku = $1
     LIMIT 1 );

$BODY$
  LANGUAGE 'sql' VOLATILE SECURITY DEFINER
  COST 100;
