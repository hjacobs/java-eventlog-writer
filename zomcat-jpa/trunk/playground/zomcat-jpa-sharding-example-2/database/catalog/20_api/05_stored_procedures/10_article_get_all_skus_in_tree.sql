create or replace function article_get_all_skus_in_tree(p_sku text)
returns setof text as
$BODY$
/*
-- $Id: 10_article_get_simple_skus.sql 444 2012-09-18 13:42:10Z tamas.eppel $
-- $HeadURL: https://svn.zalando.net/zeos-catalog/trunk/backend/database/catalog/20_api/05_stored_procedures/10_article_get_all_skus.sql $
*/
/**  Test
  set search_path=zcat_api_r12_00_40;
  select * from article_get_all_skus_in_tree('10K11A008');
*/
DECLARE
  l_model_sku_id bigint;
BEGIN
  return query
    select distinct s2.as_sku
    from zcat_data.article_sku s1
    join zcat_data.article_sku s2 on
        s2.as_model_id IN (s1.as_model_id, s1.as_config_id, s1.as_id) or -- select the subtree
       (s2.as_model_id is null and s2.as_id = s1.as_model_id) or         -- select the model itself
       (s1.as_model_id is null and s2.as_id=s1.as_id)                    -- select the model if search by a model
    where s1.as_sku = p_sku;

END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
