create or replace function article_get_simple_skus (p_sku text)
returns setof text as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**  Test
  set search_path=zcat_api_r12_00_40,public;
  select * from article_get_simple_skus('10K11A008');
*/
declare
    l_sku_id bigint;
BEGIN
  -- legacy articles may have same sku on CONFIG and MODEL
  -- we prefer MODEL but in fact it does not matter much
  -- because a legacy model sku has only one config sku
  -- so the set of simples is exactly the same
  -- see also documentation in 10_article_sku.sql
  select as_id
    into l_sku_id
    from zcat_data.article_sku
   where as_sku = p_sku
   order
      by as_sku_type desc
   limit 1;

   if not found then
     raise exception 'sku % not found', p_sku USING ERRCODE = 'Z0001';
   end if;

  return query
    select as_sku
    from zcat_data.article_sku
    where l_sku_id IN (as_id, as_model_id, as_config_id)
      and as_sku_type = 'SIMPLE';
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
