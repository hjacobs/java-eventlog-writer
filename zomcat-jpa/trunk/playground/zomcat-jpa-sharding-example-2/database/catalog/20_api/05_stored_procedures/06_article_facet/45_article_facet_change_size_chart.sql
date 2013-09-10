CREATE OR REPLACE FUNCTION article_facet_change_size_chart(
    p_article_model      article_model,
    p_scope              flow_scope
) RETURNS void AS
$BODY$
/*
  $Id$
  $HeadURL$
*/
DECLARE
    l_size_chart_group_id        int;
    l_model_sku_id               int;
    l_article_model_size_charts  size_chart[];
    l_article_simple             article_simple;
    l_article_config             article_config;
    l_article_simple_sku_id      int;
    l_matching_size              boolean;
    l_number_of_sizes            int;
BEGIN

    SELECT sizing_create_or_get_size_chart_group_id(p_article_model.size_chart_codes, p_scope) INTO l_size_chart_group_id;

    SELECT article_get_sku_id(p_article_model.model_sku, 'MODEL') INTO l_model_sku_id;

    IF NOT EXISTS(SELECT 1 FROM zcat_data.article_model WHERE am_model_sku_id = l_model_sku_id) THEN
        RAISE EXCEPTION 'Article model with sku % not found.', p_article_model.model_sku USING ERRCODE = 'Z0005';
    END IF;

    UPDATE zcat_data.article_model
       SET am_size_chart_group_id = l_size_chart_group_id
     WHERE am_model_sku_id = l_model_sku_id;

    FOREACH l_article_config IN ARRAY(p_article_model.config_facets) LOOP

        FOREACH l_article_simple IN ARRAY(l_article_config.simple_facets) LOOP

            SELECT article_simple.as_simple_sku_id
              INTO l_article_simple_sku_id
              FROM zcat_data.article_simple article_simple
              JOIN zcat_data.article_sku article_sku ON article_sku.as_id = article_simple.as_simple_sku_id
             WHERE article_sku.as_sku = l_article_simple.simple_sku;

            DELETE FROM zcat_data.article_simple_size WHERE ass_article_simple_sku_id = l_article_simple_sku_id;

            l_number_of_sizes := COALESCE(array_upper(l_article_simple.size_codes, 1), 0);

            FOR i IN 1 .. l_number_of_sizes LOOP

                INSERT INTO zcat_data.article_simple_size (
                    ass_article_simple_sku_id,
                    ass_size_chart_code,
                    ass_size_code,
                    ass_created_by,
                    ass_last_modified_by,
                    ass_flow_id)
                    SELECT l_article_simple_sku_id,
                        s_size_chart_code,
                        s_code,
                        p_scope.user_id,
                        p_scope.user_id,
                        p_scope.flow_id
                    FROM unnest(ARRAY[l_article_simple.size_codes[i]]) AS size1
                    JOIN zcat_commons.size ON s_size_chart_code = size1.size_chart_code AND s_code = size1.size_code;

            END LOOP;

            PERFORM sizing_create_or_update_size_info(l_article_simple_sku_id, p_scope);

        END LOOP;

    END LOOP;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
