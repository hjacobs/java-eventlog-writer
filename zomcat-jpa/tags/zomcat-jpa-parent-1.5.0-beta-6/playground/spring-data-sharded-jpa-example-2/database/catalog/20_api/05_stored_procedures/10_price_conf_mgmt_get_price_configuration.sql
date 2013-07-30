create or replace function price_conf_mgmt_get_price_configuration (
    p_model_sku text,
    p_from      timestamptz,
    p_to        timestamptz
) returns price_configuration as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/** -- test
show search_path;

set search_path to zcat_api_r12_00_37, public;
select price_conf_mgmt_get_price_configuration ('XB621C04F', '2012-10-18T10:57:30.000+02:00', '2012-12-01T00:00:00.000+01:00');
select price_conf_mgmt_get_price_configuration ('XB621C04F', null, '2012-12-01T00:00:00.000+01:00');
select price_conf_mgmt_get_price_configuration ('XB621C04F', '2012-10-18T10:57:30.000+02:00', null);
select price_conf_mgmt_get_price_configuration ('XB621C04F', null, null);
select *
  from zcat_data.price_definition
  join zcat_data.article_sku
    on as_id = pd_sku_id
 where as_sku = 'XB621C04F-2060034000';
*/
declare
  l_price_configuration price_configuration;
  l_article_ids bigint[];
  l_sku_ids bigint[];
  l_sku_id bigint;
  l_price_definitions price_definition[];
begin

    p_from := date_trunc('second', p_from);
    p_to := date_trunc('second', p_to);

    raise info 'called price_conf_mgmt_get_price_configuration % % %', p_model_sku, p_from::timestamp, p_to::timestamp;
    -- resolve simple sku id
    select as_id into l_sku_id from zcat_data.article_sku where as_sku = p_model_sku and as_sku_type = 'MODEL';
    if not found then
        raise exception 'SKU  % not found', p_model_sku USING ERRCODE = 'Z0001';
    end if;

    l_sku_ids := ARRAY(select as_id from zcat_data.article_sku where as_model_id = l_sku_id);
    raise info 'sku ids = % ', l_sku_ids;

    l_article_ids := ARRAY[l_sku_id] || l_sku_ids;
    raise info 'article ids = % ', l_article_ids;

    l_price_definitions := ARRAY(
        select distinct ROW(pd_id,
                as_sku,
                ROW(pl_id, pl_level, pl_name, pl_is_promotional, pl_is_layouted, pl_is_fallback)::price_level,
                pd_price,
                pd_appdomain_id,
                pd_country_code,
                date_trunc('second', pd_start_date),
                date_trunc('second', pd_end_date),
                pd_partner_id,
                CASE WHEN plrc_id is null THEN null::price_level_reason_code
                    ELSE ROW (plrc_id, plrc_value)::price_level_reason_code END )
          from zcat_data.price_definition
            join zcat_commons.price_level on pl_id = pd_price_level_id
            join zcat_data.article_sku on as_id = pd_sku_id
            LEFT JOIN zcat_data.price_level_reason_code ON plrc_id = pd_price_level_reason_code_id
         where pd_sku_id = ANY(l_article_ids)
            and
                ( p_from is not null and p_to is not null
                    and
                    ( ( date_trunc('second', pd_start_date) between p_from and p_to )
                            or ( date_trunc('second', pd_end_date) between p_from and p_to ) )
                        or
                    ( ( p_from between date_trunc('second', pd_start_date) and date_trunc('second', pd_end_date) )
                            or ( p_to between date_trunc('second', pd_start_date) and date_trunc('second', pd_end_date) ) )
                )

                or ( p_from is not null and p_to is null and p_from <= date_trunc('second', pd_end_date) )
                or ( p_from is null and p_to is not null and p_to >= date_trunc('second', pd_start_date) )
                or ( p_from is null and p_to is null )
    );

    raise info 'definitions %', l_price_definitions;

    l_price_configuration := ROW(l_price_definitions)::price_configuration;

    raise info 'configuration %', l_price_configuration;
    return l_price_configuration;
end
$BODY$

language plpgsql
  volatile
  security definer
  cost 100;
