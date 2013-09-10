create or replace function article_facet_get_articles (
    text[],
    text
) returns setof article_model as
$BODY$
-- $Id$
-- $HeadURL$
    /* this must find all simples referenced by the given sku
     i.e. if given simple, only the specific simple
                 config, all underlying simples, or only the config if no simple available
                 model, all simples of all underlying configs or only the model if no config available
    */
    with all_simples as (
        select distinct
            simple_id,
            config_id,
            model_id,
            simple_sku,
            config.as_sku as config_sku,
            model.as_sku as model_sku
        from (
            select  simples.as_id as simple_id,
                simples.as_config_id as config_id,
                simples.as_model_id as model_id,
                simples.as_sku as simple_sku

            from zcat_data.article_sku simples
                join zcat_data.article_sku top on top.as_id in (simples.as_id, simples.as_config_id, simples.as_model_id)
            where simples.as_sku_type = 'SIMPLE'
                and top.as_sku = ANY($1)

            union all
            select  null as simple_id,
                config.as_id as config_id,
                config.as_model_id as model_id,
                null as simple_sku

            from zcat_data.article_sku config
                join zcat_data.article_sku top on top.as_id in (config.as_id, config.as_model_id)
            where config.as_sku_type = 'CONFIG'
                and top.as_sku = ANY($1)
                and not exists (select 1 from zcat_data.article_sku where as_sku_type = 'SIMPLE' and top.as_id in (as_config_id, as_model_id) )
            union all
            select  null as simple_id,
                null as config_id,
                model.as_id as model_id,
                null as simple_sku

            from zcat_data.article_sku model
                join zcat_data.article_sku top on top.as_id = model.as_id
            where model.as_sku_type = 'MODEL'
                and top.as_sku = ANY($1)
                and not exists (select 1 from zcat_data.article_sku where as_sku_type = 'CONFIG' and top.as_id in (as_model_id) )
        ) t
            left join zcat_data.article_sku simple on simple.as_id = t.simple_id
            left join zcat_data.article_sku config on config.as_id = t.config_id
            left join zcat_data.article_sku model on model.as_id = t.model_id

    )

    select
        am_version,
        model_sku,
        am_name,
        am_brand_code,
        am_commodity_group_code,
        am_target_group_set,
        am_description,
        size_charts, -- ARRAY (select code from sizing_get_charts_by_group(am_size_chart_group_id)),
        am_main_supplier_code,
        am_is_globally_rebateable,
        am_is_risk_article,
        am_is_commission_article,
        model_sales_channels_release,
        nullif (array_agg (article_config_data), ARRAY[null::article_config])
    from (
        select
            case when config_sku is not null then
                ROW (
                    ac_version,
                    config_sku,
                    ac_first_season_code,
                    ac_season_code,
                    subseason_id,
                    ac_main_color_code,
                    ac_second_color_code,
                    ac_third_color_code,
                    ac_main_material_code,
                    ac_is_reloaded_article,
                    ac_is_keystyle,
                    ac_keystyle_delivery_date,
                    ac_is_disposition_locked,
                    ac_main_supplier_code,
                    ac_is_globally_rebateable,
                    ac_is_risk_article,
                    ac_is_commission_article,
                    ac_is_key_value_item,
                    pattern_id,
                    config_sales_channels_release,
                    nullif (array_agg (article_simple_data), ARRAY[null::article_simple])
                )::article_config
            else
                null::article_config
            end as article_config_data,
            am_version,
            model_sku,
            am_name,
            am_brand_code,
            am_commodity_group_code,
            am_target_group_set,
            am_description,
            size_charts,
            am_main_supplier_code,
            am_is_globally_rebateable,
            am_is_risk_article,
            am_is_commission_article,
            model_sales_channels_release
        from (

            SELECT
                case when simple_sku is not null then
                    ROW(as_version,
                        simple_sku,
                        as_is_zalando_article,
                        as_is_partner_article,
                        as_is_globally_rebateable,
                        as_is_risk_article,
                        (select ase_ean::text
                           from zcat_data.article_simple_ean
                          where ase_simple_sku_id = sku.simple_id and ase_is_active
                           ),
                        ARRAY(select ase_ean::text
                                from zcat_data.article_simple_ean
                               where ase_simple_sku_id = sku.simple_id and not ase_is_active
                               order by ase_valid_from),
                        ROW(
                            simple_sales_channel.ascr_version,
                            simple_sales_channel.ascr_is_lounge,
                            simple_sales_channel.ascr_is_offline_outlet,
                            simple_sales_channel.ascr_is_resale,
                            simple_sales_channel.ascr_is_emeza,
                            simple_sales_channel.ascr_is_kiomi
                        )::sales_channels_release,

                        ARRAY(SELECT code from sizing_get_article_simple_sizes(article_simple.as_simple_sku_id))::size_code[]
                    )::article_simple
                else
                   null::article_simple
                end as article_simple_data,
                ac_version,
                config_sku,
                ac_first_season_code,
                ac_season_code,
                CASE WHEN config_subseason.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                ROW(
                    'SUB_SEASON',
                    config_subseason.ov_code
                )::option_value_type_code END as subseason_id,
                ac_main_color_code,
                ac_second_color_code,
                ac_third_color_code,
                ac_main_material_code,
                ac_is_reloaded_article,
                ac_is_keystyle,
                ac_keystyle_delivery_date,
                ac_is_disposition_locked,
                ac_main_supplier_code,
                ac_is_globally_rebateable,
                ac_is_risk_article,
                ac_is_commission_article,
                ac_is_key_value_item,
                CASE WHEN config_pattern.ov_code IS NULL THEN NULL::option_value_type_code ELSE
                ROW(
                    'PATTERN',
                    config_pattern.ov_code
                )::option_value_type_code END as pattern_id,
                ROW(
                    config_sales_channel.ascr_version,
                    config_sales_channel.ascr_is_lounge,
                    config_sales_channel.ascr_is_offline_outlet,
                    config_sales_channel.ascr_is_resale,
                    config_sales_channel.ascr_is_emeza,
                    config_sales_channel.ascr_is_kiomi
                )::sales_channels_release as config_sales_channels_release,
                am_version,
                model_sku,
                am_name,
                am_brand_code,
                am_commodity_group_code,
                am_target_group_set,
                am_description,
                --null::size_chart[] as size_charts, --
                ARRAY (select code from sizing_get_charts_by_group(am_size_chart_group_id)) as size_charts,
                am_main_supplier_code,
                am_is_globally_rebateable,
                am_is_risk_article,
                am_is_commission_article,
                ROW(
                    model_sales_channel.ascr_version,
                    model_sales_channel.ascr_is_lounge,
                    model_sales_channel.ascr_is_offline_outlet,
                    model_sales_channel.ascr_is_resale,
                    model_sales_channel.ascr_is_emeza,
                    model_sales_channel.ascr_is_kiomi)::sales_channels_release as model_sales_channels_release
            FROM all_simples sku
                join zcat_data.article_model on sku.model_id = article_model.am_model_sku_id
                left join zcat_data.article_config on sku.config_id = article_config.ac_config_sku_id
                left join zcat_data.article_simple on sku.simple_id = article_simple.as_simple_sku_id
                left join zcat_data.article_sales_channels_release simple_sales_channel on simple_sales_channel.ascr_article_sku_id = sku.simple_id
                left join zcat_data.article_sales_channels_release config_sales_channel on config_sales_channel.ascr_article_sku_id = sku.config_id
                left join zcat_data.article_sales_channels_release model_sales_channel on model_sales_channel.ascr_article_sku_id = sku.model_id
                left join zcat_option_value.pattern config_pattern on config_pattern.ov_id = ac_pattern_id
                left join zcat_option_value.sub_season config_subseason on config_subseason.ov_id = ac_sub_season_id

        ) t
        group by
            ac_version,
            config_sku,
            ac_first_season_code,
            ac_season_code,
            subseason_id, --(SELECT master_data_get_option_value_type_code_by_id(ac_sub_season_id)),
            ac_main_color_code,
            ac_second_color_code,
            ac_third_color_code,
            ac_main_material_code,
            ac_is_reloaded_article,
            ac_is_keystyle,
            ac_keystyle_delivery_date,
            ac_is_disposition_locked,
            ac_main_supplier_code,
            ac_is_globally_rebateable,
            ac_is_risk_article,
            ac_is_commission_article,
            ac_is_key_value_item,
            pattern_id,
            config_sales_channels_release, -- (SELECT article_get_sales_channels_release(config_sku.as_id)),
            am_version,
            model_sku,
            am_name,
            am_brand_code,
            am_commodity_group_code,
            am_target_group_set,
            am_description,
            size_charts, -- ARRAY (select code from sizing_get_charts_by_group(am_size_chart_group_id)),
            am_main_supplier_code,
            am_is_globally_rebateable,
            am_is_risk_article,
            am_is_commission_article,
            model_sales_channels_release
    ) r
    group by
        am_version,
        model_sku,
        am_name,
        am_brand_code,
        am_commodity_group_code,
        am_target_group_set,
        am_description,
        size_charts, -- ARRAY (select code from sizing_get_charts_by_group(am_size_chart_group_id)),
        am_main_supplier_code,
        am_is_globally_rebateable,
        am_is_risk_article,
        am_is_commission_article,
        model_sales_channels_release


$BODY$
language sql
    volatile
    security definer
    cost 100;
