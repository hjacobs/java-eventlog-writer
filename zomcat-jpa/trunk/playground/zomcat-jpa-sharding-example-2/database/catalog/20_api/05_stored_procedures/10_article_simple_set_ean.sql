CREATE OR REPLACE FUNCTION article_simple_set_ean(
  p_sku text,
  p_ean text
  )
  RETURNS void AS
$BODY$
-- $Id$
-- $HeadURL$
/**
 * Assign the EAN to the simple SKU
 *
 * @ExpectedExecutionTime 100 ms
 * @ExpectedExecutionFrequency Daily
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from article_simple_set_ean('AD112A0A5-8500065000', '4051935585540'::EAN13);
 rollback;

 */
BEGIN
  UPDATE zcat_data.article_simple_ean
     SET ase_is_active = false
   WHERE ase_simple_sku_id =
        (SELECT as_id
           FROM zcat_data.article_sku
          WHERE as_sku = p_sku
            AND as_sku_type = 'SIMPLE');

  INSERT INTO zcat_data.article_simple_ean (
    ase_ean,
    ase_simple_sku_id,
    ase_valid_from
  )
  SELECT p_ean  ::EAN13,
         as_id,
         now()
    FROM zcat_data.article_sku
   WHERE as_sku = p_sku
     AND as_sku_type = 'SIMPLE';
END

$BODY$
LANGUAGE plpgsql
    VOLATILE SECURITY DEFINER
    COST 100;
