create or replace function get_partners ()
  returns setof partner as
$BODY$
/*
-- $Id: 10_get_partners.sql 1485 2013-01-10 08:45:41Z jan.gorman $
-- $HeadURL: https://svn.zalando.net/reboot-libs/partner-manager/database/commons/20_api/05_stored_procedures/10_get_partners.sql $
*/
BEGIN
  return query
      select p_id, p_shipping_countries, p_appdomain_ids, p_name, true, p_stock_id
        from zz_commons.partner;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
