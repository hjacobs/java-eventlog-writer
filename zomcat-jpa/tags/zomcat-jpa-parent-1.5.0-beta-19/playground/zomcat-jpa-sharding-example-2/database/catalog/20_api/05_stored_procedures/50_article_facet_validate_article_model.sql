CREATE OR REPLACE FUNCTION article_facet_validate_article_model(p_article_model article_model)
    RETURNS constraint_violation_wrapper AS
$BODY$
-- $Id$
-- $HeadURL$
DECLARE
    l_violations constraint_violation_wrapper;
    l_article_config article_config;
    l_article_simple article_simple;
    l_count int;
    l_config_index int := 0;
    l_simple_index int := 0;
    l_index int := 0;
    l_code text;
    l_size_chart_codes_exist boolean := true;
    l_size_dimension_codes text[];
BEGIN

    l_violations.constraint_violations := '{}';
    l_index := 0;

    FOREACH l_code IN ARRAY(p_article_model.size_chart_codes) LOOP
      IF NOT EXISTS (
        select 1
          from zcat_commons.size_chart
         where sc_code = l_code
      ) THEN
        l_violations.constraint_violations := l_violations.constraint_violations
            || (l_code, 'size_chart_codes[' || l_index || ']', 'UNKNOWN_SIZE_CHART_CODE')::constraint_violation_value;
        l_size_chart_codes_exist := false;
      END IF;
      l_index := l_index + 1;
    END LOOP;

    IF l_size_chart_codes_exist THEN
        l_size_dimension_codes := ARRAY(select substr(code,9) from unnest(p_article_model.size_chart_codes) code);
        IF (
            select sizing_get_dimension_group_id(l_size_dimension_codes) is null
        ) THEN
            l_violations.constraint_violations := l_violations.constraint_violations
                || (l_size_dimension_codes::text, 'size_chart_codes', 'INVALID_SIZE_DIMENSION_GROUP')::constraint_violation_value;
       END IF;
    END IF;

    IF NOT EXISTS (
       select 1
         from zcat_commons.commodity_group
        where cg_code = p_article_model.commodity_group_code
    ) THEN
        l_violations.constraint_violations := l_violations.constraint_violations
            || (p_article_model.commodity_group_code, 'commodity_group_code', 'UNKNOWN_COMMODITY_GROUP_CODE')::constraint_violation_value;
    END IF;

    RETURN l_violations;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
