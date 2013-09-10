CREATE OR REPLACE FUNCTION sku_rollback_config_sku(
    p_model_sku       text,
    p_counter         integer
)
returns void as
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Selects a next value for a given config combination. The counter is based on uniqueness of p_model_sku
 * and p_color_family.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency 10.000 per day
 */
/**  Test
  set search_path=zcat_api_r13_00_07,public;
  select * from sku_rollback_config_sku('A', 0);
*/
$BODY$
DECLARE
BEGIN
    -- first try to insert a fresh new combination (start counting)
    DELETE FROM zcat_data.sku_config_counter
          WHERE scc_model_sku = p_model_sku
            AND scc_counter = p_counter;
END;
$BODY$
language plpgsql
    volatile security definer
    cost 100;
