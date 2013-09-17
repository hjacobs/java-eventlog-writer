create or replace function price_conf_mgmt_delete_price_definition (
    p_id            bigint
) returns void as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN
    perform price_conf_mgmt_delete_price_definitions(ARRAY[p_id]);
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
