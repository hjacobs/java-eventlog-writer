CREATE OR REPLACE FUNCTION ean_create_or_update_number_range(
  p_number_range ean_number_range,
  p_scope        flow_scope
)
returns integer as
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Create or update an ean range.
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency twice a year
 */
/**  Test
  set search_path=zcat_api_r13_00_07,public;
  select * from ean_create_or_update_number_range(ROW(null,'name',true,'ZALANDO','000000000000?'::EAN13,'000000000010?'::EAN13,1,null)::ean_number_range, 'user', 'flowId');
**/
$BODY$
DECLARE
    l_ret_id integer;
BEGIN

   IF p_number_range.id IS NULL THEN
       -- insert
       -- first try to insert a fresh new combination (start counting)
       INSERT INTO zcat_commons.ean_number_range
                  (enr_created,
                   enr_created_by,
                   enr_last_modified,
                   enr_last_modified_by,
                   enr_flow_id,
                   enr_name,
                   enr_active,
                   enr_ean_number_range_type,
                   enr_start_ean,
                   enr_end_ean,
                   enr_increase_by)
           VALUES (now(),
                   p_scope.user_id,
                   now(),
                   p_scope.user_id,
                   p_scope.flow_id,
                   p_number_range.name,
                   p_number_range.active,
                   p_number_range.ean_number_range_type,
                   p_number_range.start_ean::EAN13,
                   p_number_range.end_ean::EAN13,
                   p_number_range.increase_by)
         RETURNING enr_id INTO l_ret_id;
   ELSE
       -- update
       -- first try to insert a fresh new combination (start counting)
       UPDATE zcat_commons.ean_number_range
          SET enr_last_modified         = now(),
              enr_last_modified_by      = p_scope.user_id,
              enr_flow_id               = p_scope.flow_id,
              enr_name                  = p_number_range.name,
              enr_active                = p_number_range.active,
              enr_ean_number_range_type = p_number_range.ean_number_range_type,
              enr_start_ean             = p_number_range.start_ean::EAN13,
              enr_end_ean               = p_number_range.end_ean::EAN13,
              enr_increase_by           = p_number_range.increase_by
        WHERE enr_id = p_number_range.id;

        l_ret_id := p_number_range.id;
    END IF;

    RETURN l_ret_id;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
