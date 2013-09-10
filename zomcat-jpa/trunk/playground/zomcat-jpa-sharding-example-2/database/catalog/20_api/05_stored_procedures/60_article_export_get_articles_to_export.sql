--set search_path to zcat_api_r13_00_17, zz_commons, public;
CREATE OR REPLACE FUNCTION article_export_get_articles_to_export (
    p_sku       text,
    p_minutes   integer,
    p_limit     integer,
    p_offset    integer
) returns setof solr_article
AS
$BODY$
-- $Id$
-- $HeadUrl: $
/* -- test

set search_path to zcat_api_r13_00_12, public

select * from article_export_get_articles_to_export (null, null, 1000000, 0)
where simple_sku like '12B53A002%'


--supplier is not null


*/
DECLARE
    l_from timestamp;
BEGIN

    if p_minutes is not null then
        l_from := now () - p_minutes::interval;
    end if;

    return query
    with skus as (

       select distinct
            simple_sku_id,
            config_sku_id,
            model_sku_id,
            simple_sku,
            config.as_sku as config_sku,
            model.as_sku as model_sku
        from (
                select simples.as_id as simple_sku_id,
                    simples.as_config_id as config_sku_id,
                    simples.as_model_id as model_sku_id,
                    simples.as_sku as simple_sku
                from zcat_data.article_sku simples
                    --join zcat_data.article_sku top on top.as_id in (simples.as_id, simples.as_config_id, simples.as_model_id)
                where simples.as_sku_type = 'SIMPLE'
                order by model_sku_id, config_sku_id, simple_sku_id
                limit p_limit
                offset p_offset
            ) t
            join zcat_data.article_sku model on model.as_id = model_sku_id
            join zcat_data.article_sku config on config.as_id = config_sku_id
    ),
    articles as (

        select simple_sku,
            config_sku,
            model_sku,
            simple_sku_id,
            config_sku_id,
            model_sku_id,
            am_commodity_group_code as commodity_group_code,
            am_name as name,

            as_is_zalando_article as is_zalando_article,
            as_is_partner_article as is_partner_article,
            coalesce (ac_is_commission_article, am_is_commission_article) as is_commission_article,
            ac_main_color_code as main_color_code,
            ac_second_color_code as second_color_code,
            ac_third_color_code as third_color_code,
            am_brand_code as brand_code,
            am_brand_code as brand_code_original,
            ac_season_code as season_code,
            ac_main_material_code as material_code

        from skus sku
            join zcat_data.article_simple simple on sku.simple_sku_id = as_simple_sku_id
            join zcat_data.article_config config on ac_config_sku_id = sku.config_sku_id
            join zcat_data.article_model model on am_model_sku_id = sku.model_sku_id
        where l_from is null or (
            simple.as_last_modified > l_from
            or config.ac_last_modified > l_from
            or model.am_last_modified > l_from
            )
    )

    select articles.simple_sku as SKU,
        articles.config_sku as CONFIGSKU,
        articles.model_sku as ASKU,
        ase_ean::text AS EAN,
        articles.name as descr, -- name == description. The ERP legacy lives on.
        articles.is_zalando_article as zalando_item,
        articles.is_partner_article as partner_item,
        articles.is_commission_article as commission_item,
        articles.main_color_code as color_code,
        articles.second_color_code as second_color_code,
        articles.third_color_code as third_color_code,
        articles.brand_code as brand_code,
        articles.brand_code_original as brand_code_original,
        articles.season_code as season_code,
        (select ass_size_code
           from zcat_data.article_simple_size
           join zcat_commons.size
             on ass_size_chart_code = s_size_chart_code
            and ass_size_code = s_code
           join zcat_commons.size_chart on s_size_chart_code = sc_code
           join zcat_commons.size_dimension on sc_dimension_code = sd_code
           join zcat_commons.size_dimension_group_binding on sdgb_code = sd_code
          where ass_article_simple_sku_id = articles.simple_sku_id
            and sdgb_position = 2 -- = length_code
          limit 1) as length_code,
        (select ass_size_code
           from zcat_data.article_simple_size
           join zcat_commons.size
             on ass_size_chart_code = s_size_chart_code
            and ass_size_code = s_code
           join zcat_commons.size_chart on s_size_chart_code = sc_code
           join zcat_commons.size_dimension on sc_dimension_code = sd_code
           join zcat_commons.size_dimension_group_binding on sdgb_code = sd_code
          where ass_article_simple_sku_id = articles.simple_sku_id
            and sdgb_position = 1 -- = size_code
          limit 1) as size_code,
        articles.commodity_group_code as group_code,
        (select ass_size_chart_code
           from zcat_data.article_simple_size
           join zcat_commons.size
             on ass_size_chart_code = s_size_chart_code
            and ass_size_code = s_code
           join zcat_commons.size_chart on s_size_chart_code = sc_code
           join zcat_commons.size_dimension on sc_dimension_code = sd_code
           join zcat_commons.size_dimension_group_binding on sdgb_code = sd_code
          where ass_article_simple_sku_id = articles.simple_sku_id
            and sdgb_position = 2 -- = length_code
          limit 1) as length_chart_code, -- lreg_code
        (select ass_size_chart_code
           from zcat_data.article_simple_size
           join zcat_commons.size
             on ass_size_chart_code = s_size_chart_code
            and ass_size_code = s_code
           join zcat_commons.size_chart on s_size_chart_code = sc_code
           join zcat_commons.size_dimension on sc_dimension_code = sd_code
           join zcat_commons.size_dimension_group_binding on sdgb_code = sd_code
          where ass_article_simple_sku_id = articles.simple_sku_id
            and sdgb_position = 1 -- = size_code
          limit 1) as size_chart_code, -- sreg_code
        -- -- what's the use of these fieds?
        null::text as washing_code,
        articles.main_color_code as color_descr_de,
        articles.main_color_code as color_descr_en,
        b_name::text as brand_descr,

        s_name_message_key as season_descr,
        cg_name_message_key::text as group_descr,
        null::text as washing_descr,
        articles.material_code as material_code,
        (   select coalesce (afls_customs_code, coalesce (aflc_customs_code, aflm_customs_code))
            from zcat_data.article_facet_logistics_simple
                left join zcat_data.article_facet_logistics_config on articles.config_sku_id = aflc_config_sku_id
                left join zcat_data.article_facet_logistics_model on articles.model_sku_id = aflm_model_sku_id
            where articles.simple_sku_id = afls_simple_sku_id
            limit 1
        )::text as customs_code,
        (   select ac_first_season_code
            from zcat_data.article_config
            where ac_config_sku_id = articles.config_sku_id
        )::text as initial_season_code,
        null::text as initial_purchase_price,
        null::text as initial_season_description,
        (   select ov_code
            from zcat_option_value.pattern
                join zcat_data.article_config on ac_pattern_id = ov_id
                    and ac_config_sku_id = articles.config_sku_id
        ) as pattern,
        null::text as color_descr,
        -- Nasty subselect to ensure that all supplier values are kept in sync (even if empty)
        (   select array_agg(
                ROW (supplier_code,
                      supplier_original,
                      afsm_article_name,
                      s_supplier_size,
                      afsm_article_code,
                      afsc_color_code,
                      afsc_color_description,
                      supplier_config_article_code,
                      supplier_simple_article_code,
                      afsc_upper_material_description,
                      ov_code
                )::solr_supplier_facet)
            from (
                select
                    -- supplier_code
                    tin.code as supplier_code, --coalesce (afss_supplier_code, coalesce (afsc_supplier_code, afsm_supplier_code)) as supplier_code,
                    null as supplier_original,         -- supplier_original
                    afsm_article_name,                 -- supplier_name
                    -- supplier_article_size
                    (   select array_to_string(array_agg(article_simple_sizes.supplier_size), 'x')
                        from sizing_get_article_simple_sizes(articles.simple_sku_id) article_simple_sizes) as s_supplier_size,
                    afsm_article_code,                 -- supplier_item_id
                    afsc_color_code,                   -- supplier_color_code
                    afsc_color_description,            -- supplier_color_description
                    coalesce(afsc_article_code, afsm_article_code) as supplier_config_article_code, -- supplier_config_sku
                    coalesce(afss_article_code, coalesce(afsc_article_code, afsm_article_code)) as supplier_simple_article_code, -- supplier_simple_sku
                    afsc_upper_material_description,   -- supplier_material
                    ov_code                            -- supplier_availability
                from
                    (   select distinct code
                        from (
                            select afss_supplier_code code
                            from zcat_data.article_facet_supplier_simple
                            where afss_simple_sku_id = articles.simple_sku_id
                            union
                            select afsc_supplier_code code
                            from zcat_data.article_facet_supplier_config
                            where afsc_config_sku_id = articles.config_sku_id
                            union
                            select afsm_supplier_code code
                            from zcat_data.article_facet_supplier_model
                            where afsm_model_sku_id = articles.model_sku_id
                        ) t1
                    ) tin
                    left join zcat_data.article_facet_supplier_simple on afss_supplier_code = tin.code
                        and afss_simple_sku_id = articles.simple_sku_id
                    left join zcat_data.article_facet_supplier_config on afsc_supplier_code = tin.code
                        and afsc_config_sku_id = articles.config_sku_id
                    left join zcat_data.article_facet_supplier_model on afsm_supplier_code = tin.code
                        and afsm_model_sku_id = articles.model_sku_id
                    left join zcat_option_value.availability on afsc_availability_id = ov_id
                group by
                    --coalesce (afss_supplier_code, coalesce (afsc_supplier_code, afsm_supplier_code)),
                    tin.code,
                    afsm_article_name,
                    afsm_article_code,
                    afsc_color_code,
                    afsc_color_description,
                    afsc_article_code,
                    afss_article_code,
                    afsc_upper_material_description,
                    ov_code
        ) t )

    from articles
        join zcat_commons.brand on b_code = articles.brand_code
        join zcat_commons.commodity_group on articles.commodity_group_code = cg_code
        left join zcat_data.article_simple_ean on ase_simple_sku_id = articles.simple_sku_id and ase_is_active
        join zcat_commons.season on zcat_commons.season.s_code = articles.season_code;

END

$BODY$

LANGUAGE plpgsql
    COST 100
    VOLATILE SECURITY DEFINER;
