CREATE OR REPLACE FUNCTION article_facet_update_article_simple_sizes(
  p_article_simple_sku_id  integer,
  p_size_codes             size_code[],
  p_scope                  flow_scope
) RETURNS void AS
  $BODY$
  /*
    $Id$
    $HeadURL$
  */
  DECLARE
    l_number_of_sizes           integer;
    l_article_model_size_charts size_chart [];
  BEGIN

-- check, that p_sizes is not empty
    l_number_of_sizes := COALESCE(array_upper(p_size_codes, 1), 0);
    IF l_number_of_sizes = 0
    THEN
      RAISE EXCEPTION 'Size list is empty';
    END IF;

-- load size charts of article model
    l_article_model_size_charts := ARRAY(
        SELECT
          sizing_get_charts_by_group(am_size_chart_group_id)
        FROM zcat_data.article_simple article_simple
          JOIN zcat_data.article_sku sku
            ON sku.as_id = article_simple.as_simple_sku_id
          JOIN zcat_data.article_model ON am_model_sku_id = sku.as_model_id
        WHERE article_simple.as_simple_sku_id = p_article_simple_sku_id
    );

    IF COALESCE(array_length(l_article_model_size_charts, 1), 0) = 0
    THEN
      RAISE EXCEPTION 'no size chart group found for simple %', p_article_simple_sku_id;
    END IF;

-- make sure given sizes match the model's size charts
    FOR i IN 1 .. l_number_of_sizes LOOP

    IF p_size_codes [i].size_chart_code IS DISTINCT FROM l_article_model_size_charts [i].code
    THEN
      RAISE EXCEPTION 'Article simple size on position % has wrong chart code. Expected: %. Actual: %.',
      i, l_article_model_size_charts [i].code, p_size_codes [i].size_chart_code;
    END IF;

    END LOOP;

-- update existing entries where the size changed
    UPDATE zcat_data.article_simple_size
    SET ass_size_code = new_size_code,
      ass_last_modified = now(),
      ass_last_modified_by = p_scope.user_id,
      ass_flow_id = p_scope.flow_id
-- TODO -> use ass_version for optimistic locking!
    FROM (
           select
             ass_article_simple_sku_id article_simple_id,
             ass_size_code             old_size_code,
             new_size.s_code           new_size_code,
             size_chart_code
           from unnest(p_size_codes)
             join zcat_data.article_simple_size
               on ass_article_simple_sku_id = p_article_simple_sku_id
             join zcat_commons.size old_size
               on old_size.s_code = ass_size_code
                  and old_size.s_size_chart_code = size_chart_code
             join zcat_commons.size new_size
               on new_size.s_code = size_code
                  and new_size.s_size_chart_code = size_chart_code
           where new_size.s_code != old_size.s_code
         ) sq
    WHERE ass_article_simple_sku_id = article_simple_id
          AND ass_size_chart_code = size_chart_code
          AND ass_size_code = old_size_code;


-- finally save or update the sizes
    FOR i IN 1 .. l_number_of_sizes LOOP
    BEGIN
      INSERT INTO zcat_data.article_simple_size (
        ass_article_simple_sku_id,
        ass_size_chart_code,
        ass_size_code,
        ass_created_by,
        ass_last_modified_by,
        ass_flow_id)
        SELECT
          p_article_simple_sku_id,
          s_size_chart_code,
          s_code,
          p_scope.user_id,
          p_scope.user_id,
          p_scope.flow_id
        FROM unnest(ARRAY [p_size_codes [i]]) AS size1
          JOIN zcat_commons.size ON s_size_chart_code = size1.size_chart_code AND s_code = size1.size_code;
      EXCEPTION
      WHEN unique_violation
        THEN
-- this ass_article_simple_sku_id, ass_size_id PK already exits. update instead:
          UPDATE zcat_data.article_simple_size
          SET ass_last_modified = now(),
            ass_last_modified_by = p_scope.user_id,
            ass_flow_id = p_scope.flow_id
          FROM unnest(ARRAY [p_size_codes [i]]) AS size2
            JOIN zcat_commons.size ON s_size_chart_code = size2.size_chart_code AND s_code = size2.size_code
          WHERE ass_article_simple_sku_id = p_article_simple_sku_id
                AND ass_size_code = s_code;
    END;
    END LOOP;

  END;
  $BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
