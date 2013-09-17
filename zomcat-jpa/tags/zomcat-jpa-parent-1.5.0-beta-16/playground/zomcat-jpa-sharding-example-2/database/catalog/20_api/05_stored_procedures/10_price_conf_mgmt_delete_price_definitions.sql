create or replace function price_conf_mgmt_delete_price_definitions (
    p_ids            bigint[]
) returns void as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN
    delete from zcat_data.price_definition
     using zcat_data.price_definition_additional_info
     where pd_id = pdai_price_definition_id
       and pdai_original_price_definition_id = ANY(p_ids);

    delete from zcat_data.price_definition where pd_id = ANY(p_ids);
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
