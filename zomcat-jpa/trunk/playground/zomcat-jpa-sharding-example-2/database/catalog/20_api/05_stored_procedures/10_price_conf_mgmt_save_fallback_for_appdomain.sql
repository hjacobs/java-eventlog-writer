create or replace function price_conf_mgmt_save_fallback_for_appdomain (
    p_appdomain_id        int,
    p_fallback_sequence   int[]
) returns void as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
begin
    perform 1
    from zcat_commons.price_fallback_appdomains
    where pfa_appdomain_id = p_appdomain_id;

    if found then
        update zcat_commons.price_fallback_appdomains
        set pfa_fallback_sequence = p_fallback_sequence::smallint[]
        where pfa_appdomain_id = p_appdomain_id;
    else
        insert into zcat_commons.price_fallback_appdomains
            (pfa_appdomain_id, pfa_fallback_sequence)
        values (p_appdomain_id, p_fallback_sequence::smallint[]);
    end if;
end
$BODY$

language plpgsql
    volatile
    security definer
    cost 100;
