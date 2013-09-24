create or replace function price_archive_get_archive_entries (
) returns setof price_archive_entry as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * For testing purposes only.
 */
begin

    return query
      select simple_sku.as_sku,
             pa_app_domain_id,
             pa_partner_id,
             pa_price,
             pa_promotional_price,
             source_sku.as_sku,
             pa_source_country_code,
             pa_source_app_domain_id,
             pa_source_price_level_id,
             pa_source_price_end_date,
             source_promotional_sku.as_sku,
             pa_source_promotional_country_code,
             pa_source_promotional_app_domain_id,
             pa_source_promotional_price_level_id,
             pa_source_promotional_price_end_date,
             pa_violated_rules,
             pa_source_price_level_reason_code_id,
             pa_source_promotional_price_level_reason_code_id,
             pa_source_price_start_date,
             pa_source_promotional_price_start_date
      from zcat_data.price_archive
      join zcat_data.article_sku simple_sku
        on simple_sku.as_id = pa_simple_sku_id
      left
      join zcat_data.article_sku source_sku
        on source_sku.as_id = pa_source_sku_id
      left
      join zcat_data.article_sku source_promotional_sku
        on source_promotional_sku.as_id = pa_source_promotional_sku_id;

end
$BODY$

language plpgsql
    volatile
    security definer
    cost 100;
