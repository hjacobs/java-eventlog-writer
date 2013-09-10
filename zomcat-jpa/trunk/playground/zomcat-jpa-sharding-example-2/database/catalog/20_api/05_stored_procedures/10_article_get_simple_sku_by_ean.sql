create or replace function article_get_simple_sku_by_ean (p_ean text)
returns text as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**  Test
  set search_path=zcat_api_r12_00_40,public;
  select * from article_get_simple_sku_by_ean('0000000000000');
*/
declare
    l_sku text;
BEGIN
    -- get the article model:
    IF p_ean IS NOT NULL THEN
        select as_sku
            into l_sku
            from zcat_data.article_simple_ean
            join zcat_data.article_sku on as_id = ase_simple_sku_id
           where ase_ean = p_ean::EAN13
             AND ase_valid_from <= now()
             AND as_sku_type = 'SIMPLE'
        order by ase_valid_from desc limit 1;
    END IF;

    return l_sku;
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;
