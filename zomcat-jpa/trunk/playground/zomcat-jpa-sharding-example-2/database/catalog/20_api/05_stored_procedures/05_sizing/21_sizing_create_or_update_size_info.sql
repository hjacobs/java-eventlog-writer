CREATE OR REPLACE FUNCTION sizing_create_or_update_size_info (
    p_simple_sku_id  int,
    p_scope          flow_scope
) RETURNS void as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN

    BEGIN

        INSERT INTO zcat_data.article_simple_size_info(
            assi_created_by,
            assi_last_modified_by,
            assi_flow_id,
            assi_article_simple_sku_id,
            assi_dimension_count,
            assi_size_display1_eu,
            assi_size_display2_eu,
            assi_size_display3_eu,
            assi_displayed_size_eu,
            assi_size_display1_uk,
            assi_size_display2_uk,
            assi_size_display3_uk,
            assi_displayed_size_uk,
            assi_size_display1_us,
            assi_size_display2_us,
            assi_size_display3_us,
            assi_displayed_size_us,
            assi_size_display1_fr,
            assi_size_display2_fr,
            assi_size_display3_fr,
            assi_displayed_size_fr,
            assi_size_display1_it,
            assi_size_display2_it,
            assi_size_display3_it,
            assi_displayed_size_it,
            assi_supplier_size1,
            assi_supplier_size2,
            assi_supplier_size3,
            assi_displayed_supplier_size,
            assi_sort_key1,
            assi_sort_key2,
            assi_sort_key3)
        SELECT
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            ass_article_simple_sku_id article_simple_sku_id,
            count(*) dimension_count,
            -- eu sizes
            min(CASE WHEN sdgb_position = 1 THEN (size.s_value).eu END) size_display1_eu,
            min(CASE WHEN sdgb_position = 2 THEN (size.s_value).eu END) size_display2_eu,
            min(CASE WHEN sdgb_position = 3 THEN (size.s_value).eu END) size_display3_eu,
            min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).eu END) ||
                coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).eu END), '') ||
                coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).eu END), '') displayed_size_eu,
            -- uk sizes
            min(CASE WHEN sdgb_position = 1 THEN (size.s_value).uk END) size_display1_uk,
            min(CASE WHEN sdgb_position = 2 THEN (size.s_value).uk END) size_display2_uk,
            min(CASE WHEN sdgb_position = 3 THEN (size.s_value).uk END) size_display3_uk,
            min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).uk END) ||
                coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).uk END), '') ||
                coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).uk END), '') displayed_size_uk,
            -- us sizes
            min(CASE WHEN sdgb_position = 1 THEN (size.s_value).us END) size_display1_us,
            min(CASE WHEN sdgb_position = 2 THEN (size.s_value).us END) size_display2_us,
            min(CASE WHEN sdgb_position = 3 THEN (size.s_value).us END) size_display3_us,
            min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).us END) ||
                coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).us END), '') ||
                coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).us END), '') displayed_size_us,
            -- fr sizes
            min(CASE WHEN sdgb_position = 1 THEN (size.s_value).fr END) size_display1_fr,
            min(CASE WHEN sdgb_position = 2 THEN (size.s_value).fr END) size_display2_fr,
            min(CASE WHEN sdgb_position = 3 THEN (size.s_value).fr END) size_display3_fr,
            min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).fr END) ||
                coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).fr END), '') ||
                coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).fr END), '') displayed_size_fr,
            -- it sizes
            min(CASE WHEN sdgb_position = 1 THEN (size.s_value).it END) size_display1_it,
            min(CASE WHEN sdgb_position = 2 THEN (size.s_value).it END) size_display2_it,
            min(CASE WHEN sdgb_position = 3 THEN (size.s_value).it END) size_display3_it,
            min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).it END) ||
                coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).it END), '') ||
                coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).it END), '') displayed_size_it,
            min(CASE WHEN sdgb_position = 1 THEN size.s_supplier_size END) supplier_size1,
            min(CASE WHEN sdgb_position = 2 THEN size.s_supplier_size END) supplier_size2,
            min(CASE WHEN sdgb_position = 3 THEN size.s_supplier_size END) supplier_size3,
            min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || size.s_supplier_size END) ||
                  coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || size.s_supplier_size END), '') ||
                  coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || size.s_supplier_size END), '') displayed_supplier_size,
            min(CASE WHEN sdgb_position = 1 THEN size.s_sort_key END) sort_key1,
            min(CASE WHEN sdgb_position = 2 THEN size.s_sort_key END) sort_key2,
            min(CASE WHEN sdgb_position = 3 THEN size.s_sort_key END) sort_key3
          FROM zcat_data.article_simple_size article_simple_size
          JOIN zcat_data.article_sku article_sku on (article_sku.as_id = article_simple_size.ass_article_simple_sku_id)
          JOIN zcat_data.article_model am on (am.am_model_sku_id = article_sku.as_model_id)
          JOIN zcat_commons.size size on (size.s_code = article_simple_size.ass_size_code and size.s_size_chart_code = ass_size_chart_code)
          JOIN zcat_commons.size_chart size_chart on (size_chart.sc_code = article_simple_size.ass_size_chart_code)
          JOIN zcat_commons.size_chart_group size_chart_group on (size_chart_group.scg_id = am.am_size_chart_group_id)
          JOIN zcat_commons.size_dimension_group_binding size_chart_group_binding on (size_chart_group_binding.sdgb_group_id = scg_dimension_group_id and sdgb_code = size_chart.sc_dimension_code)
          LEFT JOIN zcat_commons.size_dimension_prefix size_dimension_prefix on (size_dimension_prefix.sdp_id = size_chart_group_binding.sdgb_prefix_id)
         WHERE ass_article_simple_sku_id = p_simple_sku_id
         GROUP BY ass_article_simple_sku_id;

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_data.article_simple_size_info
           SET
            assi_last_modified = now(),
            assi_last_modified_by = p_scope.user_id,
            assi_flow_id = p_scope.flow_id,
            assi_dimension_count = dimension_count,
            assi_size_display1_eu = size_display1_eu,
            assi_size_display2_eu = size_display2_eu,
            assi_size_display3_eu = size_display3_eu,
            assi_displayed_size_eu = displayed_size_eu,
            assi_size_display1_uk = size_display1_uk,
            assi_size_display2_uk = size_display2_uk,
            assi_size_display3_uk = size_display3_uk,
            assi_displayed_size_uk = displayed_size_uk,
            assi_size_display1_us = size_display1_us,
            assi_size_display2_us = size_display2_us,
            assi_size_display3_us = size_display3_us,
            assi_displayed_size_us = displayed_size_us,
            assi_size_display1_fr = size_display1_fr,
            assi_size_display2_fr = size_display2_fr,
            assi_size_display3_fr = size_display3_fr,
            assi_displayed_size_fr = displayed_size_fr,
            assi_size_display1_it = size_display1_it,
            assi_size_display2_it = size_display2_it,
            assi_size_display3_it = size_display3_it,
            assi_displayed_size_it  = displayed_size_it,
            assi_supplier_size1 = supplier_size1,
            assi_supplier_size2 = supplier_size2,
            assi_supplier_size3 = supplier_size3,
            assi_displayed_supplier_size = displayed_supplier_size,
            assi_sort_key1 = sort_key1,
            assi_sort_key2 = sort_key2,
            assi_sort_key3 = sort_key3
        FROM (
            SELECT
                count(*) dimension_count,
                -- eu sizes
                min(CASE WHEN sdgb_position = 1 THEN (size.s_value).eu END) size_display1_eu,
                min(CASE WHEN sdgb_position = 2 THEN (size.s_value).eu END) size_display2_eu,
                min(CASE WHEN sdgb_position = 3 THEN (size.s_value).eu END) size_display3_eu,
                min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).eu END) ||
                    coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).eu END), '') ||
                    coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).eu END), '') displayed_size_eu,
                -- uk sizes
                min(CASE WHEN sdgb_position = 1 THEN (size.s_value).uk END) size_display1_uk,
                min(CASE WHEN sdgb_position = 2 THEN (size.s_value).uk END) size_display2_uk,
                min(CASE WHEN sdgb_position = 3 THEN (size.s_value).uk END) size_display3_uk,
                min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).uk END) ||
                    coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).uk END), '') ||
                    coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).uk END), '') displayed_size_uk,
                -- us sizes
                min(CASE WHEN sdgb_position = 1 THEN (size.s_value).us END) size_display1_us,
                min(CASE WHEN sdgb_position = 2 THEN (size.s_value).us END) size_display2_us,
                min(CASE WHEN sdgb_position = 3 THEN (size.s_value).us END) size_display3_us,
                min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).us END) ||
                    coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).us END), '') ||
                    coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).us END), '') displayed_size_us,
                -- fr sizes
                min(CASE WHEN sdgb_position = 1 THEN (size.s_value).fr END) size_display1_fr,
                min(CASE WHEN sdgb_position = 2 THEN (size.s_value).fr END) size_display2_fr,
                min(CASE WHEN sdgb_position = 3 THEN (size.s_value).fr END) size_display3_fr,
                min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).fr END) ||
                    coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).fr END), '') ||
                    coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).fr END), '') displayed_size_fr,
                -- it sizes
                min(CASE WHEN sdgb_position = 1 THEN (size.s_value).it END) size_display1_it,
                min(CASE WHEN sdgb_position = 2 THEN (size.s_value).it END) size_display2_it,
                min(CASE WHEN sdgb_position = 3 THEN (size.s_value).it END) size_display3_it,
                min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).it END) ||
                    coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).it END), '') ||
                    coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || (size.s_value).it END), '') displayed_size_it,
                min(CASE WHEN sdgb_position = 1 THEN size.s_supplier_size END) supplier_size1,
                min(CASE WHEN sdgb_position = 2 THEN size.s_supplier_size END) supplier_size2,
                min(CASE WHEN sdgb_position = 3 THEN size.s_supplier_size END) supplier_size3,
                min(CASE WHEN sdgb_position = 1 THEN coalesce(size_dimension_prefix.sdp_value, '') || size.s_supplier_size END) ||
                      coalesce(min(CASE WHEN sdgb_position = 2 THEN coalesce(size_dimension_prefix.sdp_value, '') || size.s_supplier_size END), '') ||
                      coalesce(min(CASE WHEN sdgb_position = 3 THEN coalesce(size_dimension_prefix.sdp_value, '') || size.s_supplier_size END), '') displayed_supplier_size,
                min(CASE WHEN sdgb_position = 1 THEN size.s_sort_key END) sort_key1,
                min(CASE WHEN sdgb_position = 2 THEN size.s_sort_key END) sort_key2,
                min(CASE WHEN sdgb_position = 3 THEN size.s_sort_key END) sort_key3
              FROM zcat_data.article_simple_size article_simple_size
              JOIN zcat_data.article_sku article_sku on (article_sku.as_id = article_simple_size.ass_article_simple_sku_id)
              JOIN zcat_data.article_model am on (am.am_model_sku_id = article_sku.as_model_id)
              JOIN zcat_commons.size size on (size.s_code = article_simple_size.ass_size_code and size.s_size_chart_code = ass_size_chart_code)
              JOIN zcat_commons.size_chart size_chart on (size_chart.sc_code = article_simple_size.ass_size_chart_code)
              JOIN zcat_commons.size_chart_group size_chart_group on (size_chart_group.scg_id = am.am_size_chart_group_id)
              JOIN zcat_commons.size_dimension_group_binding size_chart_group_binding on (size_chart_group_binding.sdgb_group_id = scg_dimension_group_id and sdgb_code = size_chart.sc_dimension_code)
              LEFT JOIN zcat_commons.size_dimension_prefix size_dimension_prefix on (size_dimension_prefix.sdp_id = size_chart_group_binding.sdgb_prefix_id)
             WHERE ass_article_simple_sku_id = p_simple_sku_id
             GROUP BY ass_article_simple_sku_id) size_info
       WHERE assi_article_simple_sku_id = p_simple_sku_id;

    END;

END
$BODY$
language plpgsql
    volatile
    security definer
    cost 100;
