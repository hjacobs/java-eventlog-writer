CREATE OR REPLACE FUNCTION article_facet_validate_article_simple(p_config_sku text, p_article_simple article_simple, p_simple_property_path text DEFAULT '')
  RETURNS constraint_violation_wrapper AS
$BODY$
-- $Id$
-- $HeadURL$
DECLARE
    l_violations constraint_violation_wrapper;
    l_index int := 0;
    l_size_code size_code;
    l_config_sku_id int;
    l_sizes text[] := ARRAY[]::text[];
    l_model_size_chart_codes text[];
BEGIN

    l_violations.constraint_violations := '{}';

    -- CHECK size codes exists
    l_index := 0;
    FOREACH l_size_code IN ARRAY(p_article_simple.size_codes) LOOP
      IF NOT EXISTS (
        select 1
          from zcat_commons.size
         where s_code = l_size_code.size_code
           and s_size_chart_code = l_size_code.size_chart_code
      ) THEN
        l_violations.constraint_violations := l_violations.constraint_violations ||
        (p_article_simple.size_codes[l_index+1]::text, p_simple_property_path || 'size_codes[' || l_index || ']', 'UNKNOWN_SIZE_CODE')::constraint_violation_value;
      END IF;

      l_index := l_index + 1;
      l_sizes := l_sizes || (l_size_code.size_chart_code || ':' || l_size_code.size_code);
    END LOOP;

    IF p_config_sku IS NOT NULL THEN

      l_model_size_chart_codes := ARRAY(
          select sizing_get_size_chart_codes_for_chart_group_id(am_size_chart_group_id)
            from zcat_data.article_sku
            join zcat_data.article_model
              on am_model_sku_id = as_model_id
           where as_sku = p_config_sku
             and as_sku_type = 'CONFIG'
      );

      IF array_length(l_model_size_chart_codes, 1) != array_length(p_article_simple.size_codes, 1) THEN
        l_violations.constraint_violations := l_violations.constraint_violations ||
           (p_article_simple.size_codes::text,
             p_simple_property_path || 'size_codes',
             'INCONSISTENT_DIMENSION_COUNT')::constraint_violation_value;
      END IF;

      FOR l_index IN 1..array_length(l_model_size_chart_codes,1) LOOP
        IF l_model_size_chart_codes[l_index] IS DISTINCT FROM p_article_simple.size_codes[l_index].size_chart_code THEN
          l_violations.constraint_violations := l_violations.constraint_violations ||
             (p_article_simple.size_codes::text,
               p_simple_property_path || 'size_codes[' || (l_index - 1) || ']',
               'INCONSISTENT_SIZE_DATA')::constraint_violation_value;
        END IF;
      END LOOP;

      IF EXISTS (
            SELECT 1
              FROM zcat_data.article_simple_size size
              JOIN zcat_data.article_simple article_simple ON size.ass_article_simple_sku_id = article_simple.as_simple_sku_id
              JOIN zcat_data.article_sku simple_sku ON article_simple.as_simple_sku_id = simple_sku.as_id
              JOIN zcat_data.article_sku config_sku ON simple_sku.as_config_id = config_sku.as_id
             WHERE config_sku.as_sku = p_config_sku
               AND (SELECT array_agg(inner_size.ass_size_chart_code || ':' || inner_size.ass_size_code)
                      FROM zcat_data.article_simple_size inner_size
                 LEFT JOIN zcat_data.article_sku inner_simple_sku ON inner_simple_sku.as_sku = p_article_simple.simple_sku
                 LEFT JOIN zcat_data.article_simple inner_article_simple ON inner_article_simple.as_simple_sku_id = inner_simple_sku.as_id
                      WHERE size.ass_article_simple_sku_id = inner_size.ass_article_simple_sku_id
                       AND (inner_article_simple.as_simple_sku_id IS NULL OR inner_size.ass_article_simple_sku_id != inner_article_simple.as_simple_sku_id)) = l_sizes
             GROUP BY size.ass_article_simple_sku_id
        ) THEN
            l_violations.constraint_violations := l_violations.constraint_violations ||
                (null, p_simple_property_path || 'size_codes', 'DUPLICATED_SIZES')::constraint_violation_value;
        END IF;
    END IF;

    RETURN l_violations;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
