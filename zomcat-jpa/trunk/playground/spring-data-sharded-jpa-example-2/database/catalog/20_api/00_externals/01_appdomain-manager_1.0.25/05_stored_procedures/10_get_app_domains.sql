create or replace function get_app_domains (
  OUT  id                                     smallint,
  OUT code                                   varchar(255),
  OUT name                                   varchar(255),
  OUT app_url                                varchar(255),
  OUT static_url                             varchar(255),
  OUT currency                               char(3),
  OUT locale                                 varchar(8),
  OUT shipping_countries                     char(2)[],
  OUT stocks                                 smallint[] ,
  OUT root_category_id                       integer,
  OUT payment_methods                        varchar(50)[],
  OUT docdata_affiliate_id                   varchar(2),
  OUT last_modified                          timestamp,
  OUT priority                               smallint ,
  OUT appdomain_set_id                       smallint,
  OUT default_newsletter_id                  smallint,
  OUT customer_appdomain_set_id              smallint ,
  OUT is_short_sale                          boolean,
  OUT is_lounge                              boolean ,
  OUT principal_code                         varchar(20) ,
  OUT customer_principal_code                varchar(20) ,
  OUT mobile_url                             varchar(255),
  OUT is_statistical_article_number_required boolean ,
  OUT shop_id                                smallint
) RETURNS SETOF record as
$BODY$

/*
-- $Id$
-- $HeadURL$
*/

BEGIN
RETURN QUERY
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
        ad_is_statistical_article_number_required,
        ad_shop_id
    from zz_commons.appdomain;
END;

$BODY$
language plpgsql
    volatile security definer
    cost 100;
