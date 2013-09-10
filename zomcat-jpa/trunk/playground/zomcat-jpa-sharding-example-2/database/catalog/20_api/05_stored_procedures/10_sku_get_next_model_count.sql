CREATE OR REPLACE FUNCTION sku_get_next_model_count(
  p_base_model_code text
)
returns integer as
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Selects a next value for a given model combination. The counter is based on uniqueness of base_model_code.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency 10.000 per day
 */
/**  Test
  set search_path=zcat_api_r13_00_07,public;
  select * from sku_get_next_model_count('A');
*/
$BODY$
DECLARE
    l_count integer;
BEGIN
    l_count := 0;
    BEGIN
       -- first try to insert a fresh new model code (start counting)
       INSERT INTO zcat_commons.sku_model_counter
                  (smc_base_model_code, smc_counter)
           VALUES (p_base_model_code, 0);


        -- this smc_base_model_code seems to exit. increase the counter:
        EXCEPTION
            WHEN unique_violation THEN
                UPDATE zcat_commons.sku_model_counter
                   SET smc_counter = smc_counter + 1
                 WHERE smc_base_model_code = p_base_model_code
                 RETURNING smc_counter into l_count;
    END;
    RETURN l_count;
END;
$BODY$
language plpgsql
    volatile security definer
    cost 100;
