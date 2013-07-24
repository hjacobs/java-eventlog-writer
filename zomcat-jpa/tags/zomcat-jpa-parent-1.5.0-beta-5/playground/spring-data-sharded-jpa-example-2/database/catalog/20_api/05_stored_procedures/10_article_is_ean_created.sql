CREATE OR REPLACE FUNCTION article_is_ean_created(
  p_ean text,
  OUT ean text,
  OUT boolean_value boolean
  )
  RETURNS SETOF record AS
$BODY$
-- $Id$
-- $HeadURL$
/**
 * Has the supplied EAN been created?
 *
 * @ExpectedExecutionTime 200 ms
 * @ExpectedExecutionFrequency Hourly
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from article_is_ean_created('2436596899250');
 rollback;

 */
SELECT ase_ean::text,
       true
  FROM zcat_data.article_simple_ean
 WHERE ase_ean = $1::EAN13 AND ase_is_active = TRUE;
$BODY$
  LANGUAGE 'sql' STABLE SECURITY DEFINER
  COST 100;