-- used on bulk migration

create or replace function price_conf_mgmt_create_or_update_price_configuration (
    p_model_sku             text,
    p_update                price_configuration_update,
    p_delete_before_insert  boolean,
    p_erp_update            boolean,
    p_scope                 flow_scope
) returns void as
$BODY$
/* -- test
show search_path
set search_path to zcat_api_r12_00_37, public



select * from price_conf_mgmt_create_or_update_price_configuration ('B4343C03F'::text,
    ROW(ARRAY[
        ROW(null,'B4343C03F-4110ONE000',ROW(1,150,null, false),12000,1,'2000-01-01 00:00:00.0','2013-01-01 00:00:00.0',null, 3001)::price_definition
        ],
    ARRAY[]::bigint[])::price_configuration_update, true, false)


    select * from zcat_data.price_definition


select unnest(ARRAY[
        ROW(null,'B4343C03F-4110ONE000',ROW(1,150,null),13000,1,'2000-01-01 00:00:00.0','2013-01-01 00:00:00.0',1, false, 3001)::price_definition
        ]) u

6993944,6993945
select *
  from zcat_data.price_definition
  join zcat_data.article_sku
    on as_id = pd_sku_id
 where as_sku = 'B4343C03F-4110ONE000';
select * from zcat_commons.price_level

 select * --,array_agg (distinct pd_id)

 from unnest(ARRAY[
        ROW(null,'B4343C03F-4110ONE000',ROW(1,150,null),13000,1,'2000-01-01 00:00:00.0','2013-01-01 00:00:00.0',null, false, 3001)::price_definition
        ]) u
        join zcat_commons.price_level on pl_level = (u.price_level).level
        join zcat_data.article_sku on as_sku = sku
        join zcat_data.price_definition on pd_sku_id = as_id
            and partner_id is not distinct from pd_partner_id
            and pd_appdomain_id is not distinct from appdomain_id;


        select array_agg (distinct pd_id)
        from unnest (ARRAY [6996300,6996301,6996302]) u(id)
            join zcat_data.price_definition on pd_id = u.id
            join zcat_commons.price_level on pl_id = pd_price_level_id;


*/

declare
     l_now              timestamp := date_trunc ('second', now());
     l_changed_ids      bigint[];
     l_price_definition price_definition;
     l_invalid_skus     text[];
begin
    raise info 'called price_conf_mgmt_create_or_update_price_configuration % %', p_model_sku, p_update;

    -- first we check if all skus exists
    l_invalid_skus := ARRAY(
        select sku
          from unnest(p_update.price_definitions)
          left
          join zcat_data.article_sku
            on as_sku = sku
         where as_id is null
    );

    if found then
      raise exception 'skus % not found', l_invalid_skus USING ERRCODE = 'Z0001';
    end if;

    delete  from zcat_data.price_definition where pd_id = ANY (p_update.price_definitions_to_delete) ;

    /*
     * Partner prices are somewhat special since on their side there's no concept of duration of a price (all prices
     * received are valid from now until forever).
     *
     * We then select all partner prices that are currently valid upon this update we
     *
     * If we receive a price definition with value 0 we don't create a new one.
     *
     * Off course: if get p_delete_before_insert then simple wipe all out before inserting.
     */
    select array_agg (distinct pd_id)
    into l_changed_ids
    from unnest(p_update.price_definitions) u
        --join zcat_commons.price_level on pl_level = (u.price_level).level
        join zcat_data.article_sku on as_sku = sku
        join zcat_data.price_definition on pd_sku_id = as_id
            and pd_partner_id is not distinct from partner_id
            and pd_appdomain_id is not distinct from appdomain_id
    -- for erp we do need to delete all price definitions that were already there;
    -- if is not erp (partner for example) affected are only the ones on the proper price level.
        left join zcat_commons.price_level on pl_level = (price_level).level
              and pd_price_level_id = pl_id
    where pl_id is not null
       or p_erp_update;

    raise info 'price definitions affected: %', l_changed_ids;

    -- truncate partner prices
    if p_delete_before_insert then

        raise info 'deleting price definitions before inserting';
        perform price_conf_mgmt_delete_price_definitions(l_changed_ids);
    else
        -- this should happen only for partner!
        raise info 'truncating affected price definitions before inserting';

        update zcat_data.price_definition
           set pd_end_date = date_trunc('second', (l_now - '1 second'::interval))
         where pd_id = ANY(l_changed_ids) and pd_partner_id is not null;

        update zcat_data.price_definition_additional_info
           set pdai_last_modified_by = p_scope.user_id,
               pdai_last_modified = now(),
               pdai_flow_id = p_scope.flow_id
         where pdai_price_definition_id in (
                   select pd_id
                     from zcat_data.price_definition
                    where pd_id = ANY(l_changed_ids)
                      and pd_partner_id is not null);

    end if;
    -- insert all non partner prices as they are.
    for l_price_definition in
        select null,
            sku,
            ROW(pl_id, pl_level, pl_name, pl_is_promotional),
            price,
            appdomain_id,
            country_code,
            case when partner_id is not null then l_now else start_date end,
            end_date,
            null,
            partner_id
        from unnest(p_update.price_definitions) u
            join zcat_commons.price_level on pl_level = (u.price_level).level
        where price > 0 -- dont insert price definitions with value 0.
        group by sku,
             pl_id,
             pl_level,
             pl_name,
             pl_is_promotional,
             price,
             appdomain_id,
             country_code,
             start_date,
             end_date,
             partner_id
    loop
        perform price_conf_mgmt_create_or_update_price_definition(l_price_definition, p_scope);
        -- TODO: handle exceptions on individual items
    end loop;

end
$BODY$

language plpgsql
  volatile
  security definer
  cost 100;
