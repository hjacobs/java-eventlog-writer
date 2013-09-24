create or replace function article_get_all_model_skus_by_substring_query (p_sku_query text)
returns setof text as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
    get all model skus (from all shards) by a substring query.

    Test
    set search_path to zcat_api_r12_00_40, public;
    select * from article_get_all_model_skus_by_substring_query('AD542B1AR');         -- will return only AD542B1AR
    select * from article_get_all_model_skus_by_substring_query('AD5');               -- will return many AD5XXX
    select * from article_get_all_model_skus_by_substring_query('AD542B1ARXXXXXXX');  -- will return only AD542B1AR
*/
declare
BEGIN
    return query
      select as_sku
        from zcat_data.article_sku
       where
         case when character_length(as_sku) >= character_length(p_sku_query) THEN
            as_sku ilike p_sku_query || '%' ELSE
            p_sku_query ilike as_sku || '%' END
        and as_sku_type = 'MODEL';
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;