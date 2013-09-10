create or replace function article_facet_get_article_summary (
    p_sku text
) returns sales_force_article_summary as

$BODY$
/* -- test
    -- $Id$
    -- $HeadURL$

*/
declare
    l_ret sales_force_article_summary;
begin

    select as_sku,
           b_name,
           am_name,
           array_agg (ROW (coalesce(afsc_article_code, afsm_article_code), afsc_color_code)::sales_force_supplier_summary),
           ac_main_color_code
      into l_ret
      from zcat_data.article_sku
      join zcat_data.article_model on as_model_id = am_model_sku_id
      join zcat_data.article_config on as_id = ac_config_sku_id
      join zcat_commons.brand on b_code = am_brand_code
      left join zcat_data.article_facet_supplier_config on afsc_config_sku_id = as_id
      left join zcat_data.article_facet_supplier_model on afsm_model_sku_id = as_model_id
     where as_sku = p_sku and as_sku_type ='CONFIG'
     group by as_sku,
              b_name,
              am_name,
              ac_main_color_code;

    return l_ret;
end

$BODY$

language plpgsql
    volatile
    security definer
    cost 100;
