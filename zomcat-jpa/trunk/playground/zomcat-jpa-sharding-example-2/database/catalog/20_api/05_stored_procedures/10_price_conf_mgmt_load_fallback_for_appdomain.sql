create or replace function price_conf_mgmt_load_fallback_for_appdomain (
    p_appdomain_id            int,
    out r_fallback_appdomain  int
) returns setof int as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**  Test
  set search_path=zcat_api_r12_00_40,public;
  select * from price_conf_mgmt_load_fallback_for_appdomain(1);
*/
    select unnest (pfa_fallback_sequence)::int
    from zcat_commons.price_fallback_appdomains
    where pfa_appdomain_id = $1;
$BODY$
language sql
    volatile
    security definer
    cost 100;
