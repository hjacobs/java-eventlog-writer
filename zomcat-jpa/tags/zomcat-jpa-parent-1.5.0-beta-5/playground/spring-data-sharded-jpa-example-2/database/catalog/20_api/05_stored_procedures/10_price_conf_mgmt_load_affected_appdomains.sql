create or replace function price_conf_mgmt_load_affected_appdomains (
    p_appdomain_id            int,
    p_country_code            zz_commons.country_code,
    p_is_promotional          boolean,
    p_is_partner              boolean,
    out r_fallback_appdomain  int
) returns setof int as
$BODY$
DECLARE
    l_ad_is_lounge boolean;
BEGIN
/*
-- $Id$
-- $HeadURL$

  when a price in an appdomain / country changes there are prices in other appdomains that might be affected by this change
  because those appdomains fallback on the appdomain in case they don't define their own price.

  if there is no appdomain given (p_appdomain_id is null) then we find out all appdomains that are determined by the country code
  (except lounge appdomains because they are independent) and take these appdomains to calculate the affected appdomains.

  besides that there are 3 exceptional cases:

  1.) if the country is AF (moon price appdomain) then all appdomains except lounge are affected. those are fallback prices
      that serve as a price when no other price is given in a real appdomain / country.

  2.) if the price is given to a partner article: price definition is defined only for "canonical" appdomain,
      therefore we load the prices for all appdomains with the same shop_id

  3.) if we have a promotional price change then there are no price that can be used as a fallback.
      still we need to resolve appdomains by country if no appdomain is given (p_appdomain_id is null).

*/
/**  Test
  set search_path=zcat_api ,public;
  select * from price_conf_mgmt_load_affected_appdomains(1, 'DE', false, false);
*/
    IF p_country_code = 'AF' THEN
       RETURN QUERY
           select ad_id::int
             from zz_commons.appdomain
            where not ad_is_lounge;
       RETURN;
    END IF;

    IF p_is_partner THEN
        RETURN QUERY
        -- prices for partners for multiple appdomains should be set for all appdomains with the same shop ID
        SELECT ad_id::int
          FROM zz_commons.appdomain
         where ad_shop_id = p_appdomain_id;

       RETURN;
    END IF;

    IF p_is_promotional THEN
        RETURN QUERY
        -- prices in shops with multiple appdomains fallback over all appdomains in the same shop even if promotional.
        SELECT out.ad_id::int
        FROM zz_commons.appdomain ad
            join zz_commons.appdomain out on ad.ad_id = out.ad_shop_id
        WHERE (p_appdomain_id is not null and ad.ad_id = p_appdomain_id)
            OR ((p_appdomain_id is null and substr(ad.ad_locale, 4) = p_country_code::text)
                and  NOT ad.ad_is_lounge);

       RETURN;
    END IF;

    RETURN QUERY
    SELECT DISTINCT appdomain_id
      FROM (
                select pfa_appdomain_id::int appdomain_id
                  from zcat_commons.price_fallback_appdomains
                 where pfa_fallback_sequence &&
                       case when p_appdomain_id is not null
                            then ARRAY[p_appdomain_id]
                            else ARRAY(
                                   select ad_id
                                     from zz_commons.appdomain
                                    where substr(ad_locale, 4) = p_country_code::text
                                      and not ad_is_lounge
                                 )
                       end
                 union
                select ad_id::int
                  from zz_commons.appdomain
                 where (p_appdomain_id is not null and ad_id = p_appdomain_id)
                    or (p_appdomain_id is null and substr(ad_locale, 4) = p_country_code::text)
                   and not ad_is_lounge
           ) sq
     WHERE appdomain_id IS NOT null;
END;
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;
