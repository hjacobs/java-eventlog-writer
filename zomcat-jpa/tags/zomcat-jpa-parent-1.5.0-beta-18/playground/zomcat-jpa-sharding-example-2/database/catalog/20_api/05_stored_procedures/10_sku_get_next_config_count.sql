CREATE OR REPLACE FUNCTION sku_get_next_config_count(
    p_model_sku       text,
    p_color_family    text,
    p_color_1         text,
    p_color_2         text,
    p_color_3         text,
    p_material        text,
    p_pattern_code    text,
    OUT model_sku     text,
    OUT counter       integer,
    OUT generated     boolean
)
returns record as
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
  select * from sku_get_next_config_count('A', 1, 1, 1, 1);
*/
$BODY$
DECLARE
BEGIN
    counter := 0;
    generated := true;
    model_sku := p_model_sku;
    BEGIN

       -- first try to insert a fresh new combination (start counting)
       INSERT INTO zcat_data.sku_config_counter
                  (scc_model_sku,
                   scc_color_family,
                   scc_color_1,
                   scc_color_2,
                   scc_color_3,
                   scc_material,
                   scc_pattern_code,
                   scc_counter)
           VALUES (p_model_sku,
                   p_color_family,
                   p_color_1,
                   p_color_2,
                   p_color_3,
                   p_material,
                   p_pattern_code,
                   (SELECT (coalesce(max(scc_counter), -1) + 1) -- count all combinations that are stored for sku & family color
                     FROM zcat_data.sku_config_counter
                    WHERE scc_model_sku = p_model_sku
                      AND scc_color_family = p_color_family
                   ))
       RETURNING scc_counter INTO counter;

    EXCEPTION
        WHEN unique_violation THEN
          -- this combination already exits. we cannot increase the counter.
            generated := false;

            -- instead get the counter back
            SELECT scc_counter INTO counter
             FROM zcat_data.sku_config_counter
            WHERE scc_model_sku = p_model_sku
              AND scc_color_family = p_color_family
              AND scc_color_1  = p_color_1
              AND scc_color_2  = p_color_2
              AND scc_color_3  = p_color_3
              AND scc_material = p_material
              AND scc_pattern_code = p_pattern_code;
    END;
END;
$BODY$
language plpgsql
    volatile security definer
    cost 100;
