create or replace function get_shops ()
returns setof zz_commons.shop as
$BODY$
/*
-- $Id: 10_get_shops.sql 1747 2013-03-01 12:50:45Z daniel.del.hoyo $
-- $HeadURL: https://svn.zalando.net/reboot-libs/appdomain-manager/trunk/database/commons/20_api/05_stored_procedures/10_get_shops.sql $
*/
BEGIN
    return query
    select
        s_id,
        s_frontend_type,
        s_country,
        s_is_active,
        s_code,
        s_shipping_countries,
        s_payment_methods,
        s_stock_group_ids,
        s_root_category_id,
        s_principal_code,
        s_is_statistical_article_number_required
    from zz_commons.shop;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
