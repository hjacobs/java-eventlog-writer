CREATE OR REPLACE FUNCTION ean_get_next_ean(
  p_number_range_id integer,
  p_scope           flow_scope
)
returns text as
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
  select * from ean_get_next_ean(1, 'user', 'flowId');
*/
$BODY$
DECLARE
    l_last_ean EAN13;
    l_end_ean  EAN13;
BEGIN
    BEGIN

       -- first try to find any freed ean:
        BEGIN
           SELECT ere_released_ean
              INTO l_last_ean
             FROM zcat_commons.ean_released_ean
            WHERE ere_ean_number_range_id = p_number_range_id
            LIMIT 1;

            -- we found at least one enty. use - and remove it from the list.
            IF l_last_ean IS NOT NULL THEN
               DELETE FROM zcat_commons.ean_released_ean
                     WHERE ere_released_ean = l_last_ean
                       AND ere_ean_number_range_id = p_number_range_id;
           END IF;
        END;

      -- if we did not find any...
      IF l_last_ean IS NULL THEN
         -- use standard increase. get current ean:
         SELECT enr_last_ean, enr_end_ean
           INTO l_last_ean, l_end_ean
           FROM zcat_commons.ean_number_range
          WHERE enr_id = p_number_range_id;


          -- if not found raise exception
          IF NOT FOUND THEN
              raise 'invalid number_range_id %', p_number_range_id;
          END IF;

          -- if l_last_ean is null we need to use the enr_start_ean value:
          IF l_last_ean IS NULL THEN
              UPDATE zcat_commons.ean_number_range
                 SET enr_last_ean           = enr_start_ean,
                     enr_last_modified      = now(),
                     enr_last_modified_by   = p_scope.user_id,
                     enr_flow_id            = p_scope.flow_id
               WHERE enr_id = p_number_range_id
           RETURNING enr_last_ean INTO l_last_ean;
          ELSE
              -- check if l_last_ean == enr_end_ean -> we cannot generate any new ean:
              IF l_last_ean = l_end_ean THEN
                  raise 'end of number range reached. id %, last ean %', p_number_range_id, l_last_ean;
              ELSE
              -- else generate a brand new ean by increasing
                 UPDATE zcat_commons.ean_number_range
                    SET enr_last_ean            = ean_increase_ean(enr_last_ean, enr_end_ean, enr_increase_by),
                        enr_last_modified       = now(),
                        enr_last_modified_by    = p_scope.user_id,
                        enr_flow_id             = p_scope.flow_id
                  WHERE enr_id = p_number_range_id
              RETURNING enr_last_ean INTO l_last_ean;

              END IF;
          END IF;
       END IF;
    END;
    RETURN l_last_ean;
END;
$BODY$
language plpgsql
    volatile security definer
    cost 100;
