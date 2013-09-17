CREATE OR REPLACE FUNCTION price_archive_add_entries_bulk(p_entries price_archive_entry[])
  RETURNS add_price_archive_entries_result AS
$BODY$

/* --testing

SET search_path to zcat_api, public;
SET client_min_messages to debug1;

-- create test skus
SELECT * FROM article_create_simple_sku ('XXXXXXXXX', 'XXXXXXXXX-000','XXXXXXXXX-000000S000');
SELECT * FROM article_create_simple_sku ('XXXXXXXXX', 'XXXXXXXXX-000','XXXXXXXXX-000000M000');
SELECT * FROM article_create_simple_sku ('XXXXXXXXX', 'XXXXXXXXX-000','XXXXXXXXX-000000L000');

-- create price archive entries with a bulk
SELECT price_archive_add_entries_bulk (
  (ARRAY[ ('XXXXXXXXX-000000X000', 1, null, 11, 9, null, null, null, null, null, null, null, null, null, null, null, null, null),
          ('XXXXXXXXX-000000S000', 1, null, 11, 9, null, null, null, null, null, null, null, null, null, null, null, null, null),
          ('XXXXXXXXX-000000M000', 1, null, 10, 9, null, null, null, null, null, null, null, null, null, null, null, null, null),
          ('XXXXXXXXX-000000L000', 1, null, 11, 10, null, null, null, null, null, null, null, null, null, null, null, null, null),
          ('XXXXXXXXX-000000Y000', 1, null, 11, 9, null, null, null, null, null, null, null, null, null, null, null, null, null)
        ]:: price_archive_entry[]));

truncate zcat_data.price_current
truncate zcat_data.price_archive
SELECT * FROM zcat_data.price_current
SELECT * FROM zcat_data.price_archive

*/
DECLARE
  result  add_price_archive_entries_result;
BEGIN

  raise info 'received %', p_entries;

  WITH all_entries AS (
    SELECT  simple_sku,
            standard.as_id AS simple_sku_id,
            app_domain_id,
            partner_id,
            price,
            promotional_price,
            pd_price AS risk_promotional_price,
            case when source.as_is_legacy
              THEN (SELECT as_id FROM zcat_data.article_sku aa WHERE aa.as_sku = source_sku AND as_sku_type = 'CONFIG')
              else source.as_id END AS source_sku_id,
            source_country_code,
            source_app_domain_id,
            source_price_level_id,
            source_price_end_date,
            case when promotional.as_is_legacy
              THEN (SELECT as_id FROM zcat_data.article_sku aa WHERE aa.as_sku = source_promotional_sku AND as_sku_type = 'CONFIG')
              else promotional.as_id END AS source_promotional_sku_id,
            source_promotional_country_code,
            source_promotional_app_domain_id,
            source_promotional_price_level_id,
            source_promotional_price_end_date,
            violated_rules,
            source_price_level_reason_code_id,
            source_promotional_price_level_reason_code_id,
            pd_price_level_reason_code_id AS risk_promotional_price_level_reason_code_id,
            source_price_start_date,
            source_promotional_price_start_date
      FROM  unnest (p_entries) entry
      JOIN  zcat_data.article_sku standard ON standard.as_sku = simple_sku
      LEFT  JOIN zcat_data.article_sku promotional ON promotional.as_id = (select as_id from zcat_data.article_sku where as_sku = source_promotional_sku order by as_sku_type limit 1)
      LEFT  JOIN zcat_data.article_sku source ON source.as_id = (select as_id from zcat_data.article_sku where as_sku = source_sku order by as_sku_type limit 1)
      LEFT  JOIN (SELECT * FROM zcat_data.price_definition
                    LEFT JOIN zcat_commons.price_level on pd_price_level_id = pl_id) as pp on pd_sku_id = standard.as_id
                     AND pl_name = 'risk_promotional'
                     AND (pd_appdomain_id = entry.app_domain_id or (pd_appdomain_id is null and pd_country_code = (
                          select distinct (substring (ad_locale from 4 for 2))
                            from zz_commons.appdomain
                           where ad_id = app_domain_id
                           limit 1
                         )::zz_commons.country_code))
                     AND pd_partner_id is not distinct from partner_id),

  to_insert_new AS (
          SELECT  simple_sku, simple_sku_id, app_domain_id, partner_id, price, promotional_price, risk_promotional_price,
                  source_sku_id, source_country_code, source_app_domain_id,
                  source_price_level_id, source_price_end_date, source_promotional_sku_id, source_promotional_country_code,
                  source_promotional_app_domain_id, source_promotional_price_level_id, source_promotional_price_end_date,
                  violated_rules, source_price_level_reason_code_id, source_promotional_price_level_reason_code_id,
                  risk_promotional_price_level_reason_code_id,
                  source_price_start_date,
                  source_promotional_price_start_date
            FROM  all_entries
       LEFT JOIN  zcat_data.price_current ON pc_simple_sku_id = all_entries.simple_sku_id
             AND  pc_app_domain_id = all_entries.app_domain_id
             AND  pc_partner_id IS NOT DISTINCT FROM all_entries.partner_id
           WHERE  pc_id IS null
             AND  price IS NOT null     -- do not store (new) entries that have no price in the price current table
  ),

  stmt_insert_new AS (
     insert INTO  zcat_data.price_current ( pc_simple_sku_id, pc_app_domain_id, pc_partner_id, pc_price, pc_promotional_price, pc_risk_promotional_price,
                                            pc_source_sku_id, pc_source_country_code, pc_source_app_domain_id, pc_source_price_level_id,
                                            pc_source_price_end_date, pc_source_promotional_sku_id, pc_source_promotional_country_code,
                                            pc_source_promotional_app_domain_id, pc_source_promotional_price_level_id,
                                            pc_source_promotional_price_end_date, pc_violated_rules, pc_source_price_level_reason_code_id,
                                            pc_source_promotional_price_level_reason_code_id, pc_risk_promotional_price_level_reason_code_id,
                                            pc_source_price_start_date, pc_source_promotional_price_start_date)
          SELECT  simple_sku_id, app_domain_id, partner_id, price, promotional_price, risk_promotional_price, source_sku_id, source_country_code,
                  source_app_domain_id, source_price_level_id, source_price_end_date, source_promotional_sku_id, source_promotional_country_code,
                  source_promotional_app_domain_id, source_promotional_price_level_id, source_promotional_price_end_date, violated_rules,
                  source_price_level_reason_code_id, source_promotional_price_level_reason_code_id, risk_promotional_price_level_reason_code_id,
                  source_price_start_date, source_promotional_price_start_date
            FROM  to_insert_new
           WHERE  simple_sku_id IS NOT null
       RETURNING  pc_id
  ),

  to_update_current AS (
          SELECT  simple_sku, pc_id, simple_sku_id, app_domain_id, partner_id, price, promotional_price, risk_promotional_price, source_sku_id, source_country_code,
                  source_app_domain_id, source_price_level_id, source_price_end_date, source_promotional_sku_id, source_promotional_country_code,
                  source_promotional_app_domain_id, source_promotional_price_level_id, source_promotional_price_end_date, pc_created, violated_rules,
                  source_price_level_reason_code_id, source_promotional_price_level_reason_code_id, risk_promotional_price_level_reason_code_id,
                  source_price_start_date, source_promotional_price_start_date
            FROM  zcat_data.price_current
            JOIN  all_entries x
              ON  pc_app_domain_id = x.app_domain_id
             AND  pc_partner_id IS NOT DISTINCT FROM partner_id
             AND  pc_simple_sku_id = x.simple_sku_id
           WHERE  x.price IS DISTINCT FROM pc_price
              OR  x.promotional_price IS DISTINCT FROM pc_promotional_price
              OR  x.risk_promotional_price IS DISTINCT FROM pc_risk_promotional_price
              OR  x.source_price_level_id IS DISTINCT FROM pc_source_price_level_id
              OR  x.source_app_domain_id IS DISTINCT FROM pc_source_app_domain_id
              OR  x.source_country_code IS DISTINCT FROM pc_source_country_code
              OR  x.source_price_end_date IS DISTINCT FROM pc_source_price_end_date
              OR  x.source_promotional_country_code IS DISTINCT FROM pc_source_promotional_country_code
              OR  x.source_promotional_app_domain_id IS DISTINCT FROM pc_source_promotional_app_domain_id
              OR  x.source_promotional_price_level_id IS DISTINCT FROM pc_source_promotional_price_level_id
              OR  x.source_promotional_price_end_date IS DISTINCT FROM pc_source_promotional_price_end_date
              OR  x.violated_rules IS DISTINCT FROM pc_violated_rules
              OR  x.source_price_level_reason_code_id IS DISTINCT FROM pc_source_price_level_reason_code_id
              OR  x.source_promotional_price_level_reason_code_id IS DISTINCT FROM pc_source_promotional_price_level_reason_code_id
              OR  x.risk_promotional_price_level_reason_code_id IS DISTINCT FROM pc_risk_promotional_price_level_reason_code_id
              OR  x.source_sku_id IS DISTINCT FROM pc_source_sku_id
              OR  x.source_promotional_sku_id IS DISTINCT FROM pc_source_promotional_sku_id
              OR  x.source_price_start_date IS DISTINCT FROM pc_source_price_start_date
              OR  x.source_promotional_price_start_date IS DISTINCT FROM pc_source_promotional_price_start_date
  ),

  to_insert_archive AS (
          SELECT  simple_sku, simple_sku_id, app_domain_id, partner_id, pc_id, pc_price, pc_promotional_price, pc_risk_promotional_price, pc_source_sku_id, pc_source_country_code,
                  pc_source_app_domain_id, pc_source_price_level_id, pc_source_price_end_date, pc_source_promotional_sku_id, pc_source_promotional_country_code,
                  pc_source_promotional_app_domain_id, pc_source_promotional_price_level_id, pc_source_promotional_price_end_date, pc_last_modified as current_created,
                  pc_violated_rules, pc_source_price_level_reason_code_id, pc_source_promotional_price_level_reason_code_id, pc_risk_promotional_price_level_reason_code_id,
                  pc_source_price_start_date, pc_source_promotional_price_start_date
            FROM  zcat_data.price_current
            JOIN  all_entries x
              ON  pc_app_domain_id = x.app_domain_id
             AND  pc_partner_id IS NOT DISTINCT FROM partner_id
             AND  pc_simple_sku_id = x.simple_sku_id
           WHERE  x.price IS DISTINCT FROM pc_price
              OR  x.promotional_price IS DISTINCT FROM pc_promotional_price
              OR  x.risk_promotional_price IS DISTINCT FROM pc_risk_promotional_price
              OR  x.source_price_level_id IS DISTINCT FROM pc_source_price_level_id
              OR  x.source_app_domain_id IS DISTINCT FROM pc_source_app_domain_id
              OR  x.source_country_code IS DISTINCT FROM pc_source_country_code
              OR  x.source_price_end_date IS DISTINCT FROM pc_source_price_end_date
              OR  x.source_promotional_country_code IS DISTINCT FROM pc_source_promotional_country_code
              OR  x.source_promotional_app_domain_id IS DISTINCT FROM pc_source_promotional_app_domain_id
              OR  x.source_promotional_price_level_id IS DISTINCT FROM pc_source_promotional_price_level_id
              OR  x.source_promotional_price_end_date IS DISTINCT FROM pc_source_promotional_price_end_date
              OR  x.violated_rules IS DISTINCT FROM pc_violated_rules
              OR  x.source_price_level_reason_code_id IS DISTINCT FROM pc_source_price_level_reason_code_id
              OR  x.source_promotional_price_level_reason_code_id IS DISTINCT FROM pc_source_promotional_price_level_reason_code_id
              OR  x.risk_promotional_price_level_reason_code_id IS DISTINCT FROM pc_risk_promotional_price_level_reason_code_id
              OR  x.source_sku_id IS DISTINCT FROM pc_source_sku_id
              OR  x.source_promotional_sku_id IS DISTINCT FROM pc_source_promotional_sku_id
              OR  x.source_price_start_date IS DISTINCT FROM pc_source_price_start_date
              OR  x.source_promotional_price_start_date IS DISTINCT FROM pc_source_promotional_price_start_date
  ),

  stmt_update_current AS (
          UPDATE  zcat_data.price_current pc
             SET  pc_price = to_update_current.price,
                  pc_promotional_price = to_update_current.promotional_price,
                  pc_risk_promotional_price = to_update_current.risk_promotional_price,
                  pc_source_sku_id = to_update_current.source_sku_id,
                  pc_source_country_code = to_update_current.source_country_code,
                  pc_source_app_domain_id = to_update_current.source_app_domain_id,
                  pc_source_price_level_id = to_update_current.source_price_level_id,
                  pc_source_price_end_date = to_update_current.source_price_end_date,
                  pc_source_promotional_sku_id = to_update_current.source_promotional_sku_id,
                  pc_source_promotional_country_code = to_update_current.source_promotional_country_code,
                  pc_source_promotional_app_domain_id = to_update_current.source_promotional_app_domain_id,
                  pc_source_promotional_price_level_id = to_update_current.source_promotional_price_level_id,
                  pc_source_promotional_price_end_date = to_update_current.source_promotional_price_end_date,
                  pc_last_modified = now(),
                  pc_violated_rules = to_update_current.violated_rules,
                  pc_source_price_level_reason_code_id = to_update_current.source_price_level_reason_code_id,
                  pc_source_promotional_price_level_reason_code_id = to_update_current.source_promotional_price_level_reason_code_id,
                  pc_risk_promotional_price_level_reason_code_id = to_update_current.risk_promotional_price_level_reason_code_id,
                  pc_source_price_start_date = to_update_current.source_price_start_date,
                  pc_source_promotional_price_start_date = to_update_current.source_promotional_price_start_date
            FROM  to_update_current
           WHERE  pc.pc_id = to_update_current.pc_id
  ),

    stmt_insert_archive AS (
     insert INTO  zcat_data.price_archive ( pa_simple_sku_id, pa_app_domain_id, pa_partner_id, pa_price, pa_promotional_price, pa_risk_promotional_price,
                                            pa_source_sku_id, pa_source_country_code, pa_source_app_domain_id, pa_source_price_level_id,
                                            pa_source_price_end_date, pa_source_promotional_sku_id, pa_source_promotional_country_code,
                                            pa_source_promotional_app_domain_id, pa_source_promotional_price_level_id,
                                            pa_source_promotional_price_end_date, pa_created, pa_violated_rules, pa_source_price_level_reason_code_id,
                                            pa_source_promotional_price_level_reason_code_id, pa_risk_promotional_price_level_reason_code_id,
                                            pa_source_price_start_date, pa_source_promotional_price_start_date)
          SELECT  simple_sku_id, app_domain_id, partner_id, pc_price, pc_promotional_price, pc_risk_promotional_price, pc_source_sku_id, pc_source_country_code, pc_source_app_domain_id,
                  pc_source_price_level_id, pc_source_price_end_date, pc_source_promotional_sku_id, pc_source_promotional_country_code,
                  pc_source_promotional_app_domain_id, pc_source_promotional_price_level_id, pc_source_promotional_price_end_date, current_created,
                  pc_violated_rules, pc_source_price_level_reason_code_id, pc_source_promotional_price_level_reason_code_id, pc_risk_promotional_price_level_reason_code_id,
                  pc_source_price_start_date, pc_source_promotional_price_start_date
            FROM  to_insert_archive
  )

SELECT COALESCE((SELECT array_agg (ROW (simple_sku, app_domain_id, partner_id) ) FROM to_insert_new), '{}'),
       COALESCE((SELECT array_agg (ROW (simple_sku, app_domain_id, partner_id) ) FROM to_update_current), '{}'),
       COALESCE((SELECT array_agg (ROW (simple_sku, app_domain_id, partner_id) ) FROM to_insert_archive), '{}'),
       '{}'
  INTO result.inserted_into_price_current,
       result.updated_price_current,
       result.inserted_into_price_archive,
       result.deleted_from_price_current;

raise info 'inserted into price_current %', result.inserted_into_price_current;
raise info 'updated price_current %', result.updated_price_current;
raise info 'inserted into price_archive %', result.inserted_into_price_archive;
raise info 'deleted from price_current %', result.deleted_from_price_current;

RETURN result;

END
$BODY$
  LANGUAGE plpgsql VOLATILE SECURITY DEFINER
  COST 100;
