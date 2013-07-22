create or replace function price_conf_mgmt_search_sku_price_configuration_pairs (
    p_sku_query text,
    p_from_date       timestamptz,
    p_to_date         timestamptz,
    p_country_code    text,
    p_appdomain_ids   int[]
) returns setof sku_price_definitions_pair as
$BODY$
/** -- test
show search_path;
    set search_path=zcat_api;
    SELECT * FROM price_conf_mgmt_search_sku_price_configuration_pairs ( '123', null, null, null );
    SELECT * FROM price_conf_mgmt_search_sku_price_configuration_pairs ( '123', null, null, array[1,2,3] );
*/
declare
    l_app_domain_ids int[];
    l_app_domain_ids_tmp int[];
    l_appdomain_id int;
begin

    p_from_date := date_trunc('second', p_from_date);
    p_to_date := date_trunc('second', p_to_date);

    raise info 'called price_conf_mgmt_search_sku_price_configuration_pairs p_sku_query %, p_from_date %, p_to_date %, p_appdomain_ids %',
        p_sku_query,
        p_from_date,
        p_to_date,
        p_appdomain_ids;


    if p_appdomain_ids is not null then
        for l_appdomain_id in
            select p_appdomain_id
            from unnest(p_appdomain_ids) as u(p_appdomain_id)
        loop
            select array_agg(x) into l_app_domain_ids_tmp from price_conf_mgmt_load_fallback_for_appdomain(l_appdomain_id) x;
            l_app_domain_ids := l_app_domain_ids || l_app_domain_ids_tmp || l_appdomain_id;
        end loop;

        raise info 'using appdomains (incl. fallback) %', l_app_domain_ids;
    end if;

    -- now select the ast/branch of the selected tree.
    -- if we match a model - get the whole tree
    -- if we match one or more configs, get the configs with all simples inside
    -- if we match one or more simples, get the simples with all simples inside
    return query
    select
    -- select the sku:
    outer_as.as_sku,
    outer_as.as_sku_type,
    -- select all belonging price definitions:
    (
      select array_agg(x) from
      (
        select (
          pd_id,
          as_sku,
          ROW (pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level,
          pd_price,
          pd_appdomain_id,
          pd_country_code,
          date_trunc('second', pd_start_date),
          date_trunc('second', pd_end_date),
          pd_partner_id,
          ROW (plrc_id, plrc_value)::price_level_reason_code
        )::price_definition x
        from zcat_data.price_definition
        join zcat_data.article_sku on as_id = pd_sku_id
        join zcat_commons.price_level on pl_id = pd_price_level_id
   LEFT JOIN zcat_data.price_level_reason_code ON plrc_id = pd_price_level_reason_code_id
       where pd_sku_id = outer_as.as_id
         and pd_partner_id IS NULL  -- remove this if partner price definitions should be included in the admin
                                    -- search result
         and CASE WHEN p_country_code   IS NOT NULL THEN pd_country_code::text = p_country_code             ELSE true END
         and CASE WHEN p_to_date        IS NOT NULL THEN date_trunc('second', pd_start_date) < p_to_date    ELSE true END
         and CASE WHEN p_from_date      IS NOT NULL THEN date_trunc('second', pd_end_date) > p_from_date    ELSE true END
         and CASE WHEN l_app_domain_ids IS NOT NULL THEN pd_appdomain_id = any(l_app_domain_ids)            ELSE true END
       ) as z
    )::price_definition[]

    from zcat_data.article_sku as outer_as
   where
         case when character_length(outer_as.as_sku) >= character_length(p_sku_query) THEN
            outer_as.as_sku ilike p_sku_query || '%' ELSE
            p_sku_query ilike outer_as.as_sku || '%' END
    group by outer_as.as_id, outer_as.as_sku
    order by outer_as.as_sku;

end
$BODY$

language plpgsql
  volatile
  security definer
  cost 100;
