create or replace function article_get_skus(p_offset int, p_limit int)
returns setof text as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Selects all skus.
 *
 *
 * @ExpectedExecutionTime depends on p_limit parameter
 * @ExpectedExecutionFrequency EveryJmxMBeanRequest
 */
/**  Test
  set search_path=zcat_api_r12_00_48;
  select * from article_get_skus(0, 1000);
*/
begin

  return query
    select as_sku
      from zcat_data.article_sku
     limit p_limit
    offset p_offset;

end
$BODY$
language plpgsql
    volatile security definer
    cost 100;
