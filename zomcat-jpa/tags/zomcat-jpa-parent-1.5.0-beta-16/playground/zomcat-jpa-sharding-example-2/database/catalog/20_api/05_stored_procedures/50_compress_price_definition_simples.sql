--set search_path to zcat_api_r12_00_36, public;
create or replace function compress_price_definition_simples ()
returns void as
$BODY$

declare
    r record;
begin

    for r in
        /*
        select pd_ids, pd_price_level_id, pd_price, pd_appdomain_id, pd_start_date, pd_end_date, pd_promotional, as_config_id, c, max (c)
        from (
            select array_agg (pd_id) as pd_ids, pd_price_level_id, pd_price, pd_appdomain_id, pd_start_date, pd_end_date, pd_promotional, as_config_id, count(pd_id) as c
            from zcat_data.price_definition_simple
                join zcat_data.article_simple on pd_sku_id = as_simple_sku_id
                join zcat_data.article_config on as_config_id = ac_id
            group by pd_price_level_id, pd_price, pd_appdomain_id, pd_start_date, pd_end_date, pd_promotional, as_config_id
            having count(pd_id) > 1
            order by as_config_id, count(pd_id)
            ) t
        group by pd_ids, pd_price_level_id, pd_price, pd_appdomain_id, pd_start_date, pd_end_date, pd_promotional, as_config_id, c
        having c = max (c)
        */

        -- selects all whose price_definitions cover all simples for the existing configs (for 1 simple per single is optional)
        select array_agg (pd_id) as pd_ids,
               pd_price_level_id,
               pd_price,
               pd_appdomain_id,
               date_trunc('second', pd_start_date),
               date_trunc('second', pd_end_date),
               pd_promotional,
               as_config_id,
               count(pd_id) as c,
               q.qty
        from zcat_data.price_definition
            join zcat_data.article_sku on pd_sku_id = as_id
          --  join zcat_data.article_config on as_config_id = ac_id
            join (  select count (1) as qty,
                           as_id as sku_id
                    from zcat_data.article_sku
                    where as_sku_type = 'CONFIG'
                    group by as_config_id) as q on q.sku_id = as_config_id
        where as_sku_type = 'CONFIG'
        group by pd_price_level_id, pd_price, pd_appdomain_id, pd_start_date, pd_end_date, pd_promotional, as_id, q.qty
        having count(pd_id) = q.qty --and count(pd_id) > 1
        order by as_config_id, count(pd_id)
    loop
        raise info 'foo: %', r;

        insert into zcat_data.price_definition_config (
             pd_sku_id,
             pd_price_level_id,
             pd_price,
             pd_appdomain_id,
             pd_start_date,
             pd_end_date,
             pd_promotional)
        values (
            r.as_config_id,
            r.pd_price_level_id,
            r.pd_price,
            r.pd_appdomain_id,
            r.pd_start_date,
            r.pd_end_date,
            r.pd_promotional
        );

        perform price_conf_mgmt_delete_price_definitions(r.pd_ids);

    end loop;

end
$BODY$

language plpgsql
  volatile
  security definer
  cost 100;
