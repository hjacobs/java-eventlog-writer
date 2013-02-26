create or replace function get_app_domains ()
returns setof zz_commons.app_domain as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN
    return query
    select
        ad_id,
        ad_code,
        ad_name,
        ad_app_url,
        ad_static_url,
        ad_currency,
        ad_locale,
        ad_shipping_countries,
        ad_stocks,
        ad_root_category_id,
        ad_payment_methods,
        ad_docdata_affiliate_id,
        ad_last_modified,
        ad_priority,
        ad_appdomain_set_id,
        ad_default_newsletter_id,
        ad_customer_appdomain_set_id,
        ad_is_short_sale,
        ad_is_lounge,
        ad_principal_code,
        ad_customer_principal_code,
        ad_mobile_url,
        ad_is_statistical_article_number_required
    from zz_commons.appdomain;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;