create or replace function price_conf_mgmt_load_fallback_sequence_for_appdomains()
returns setof price_fallback_appdomains as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**  Test
  set search_path=zcat_api_r12_00_40,public;
  select * from price_conf_mgmt_load_fallback_sequence_for_appdomains();
*/
BEGIN
    return query
    select pfa_appdomain_id::int, pfa_fallback_sequence::int[]
    from zcat_commons.price_fallback_appdomains;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
