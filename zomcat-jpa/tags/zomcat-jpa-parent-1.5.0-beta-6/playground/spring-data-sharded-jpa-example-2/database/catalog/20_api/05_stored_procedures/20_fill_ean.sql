create or replace function fill_ean (
    p_simple_sku    text,
    p_ean           text
) returns void as
$BODY$
/*
    -- $Id$
    -- $HeadURL$
*/
DECLARE
    l_sku_id integer;
    l_ena_id integer;
BEGIN

    select as_id
      into l_sku_id
      from zcat_data.article_sku
     where as_sku_type = 'SIMPLE'
       and as_sku = p_simple_sku;

    select ase_id
      into l_ena_id
      from zcat_data.article_simple_ean
     where ase_simple_sku_id = l_sku_id;

    if not found then
        insert into zcat_data.article_simple_ean (ase_simple_sku_id, ase_ean) values (l_sku_id, p_ean::ean13);
    else
        update zcat_data.article_simple_ean
        set ase_ean = p_ean::ean13
        where ase_id = l_ena_id;
    end if;

END;
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;
