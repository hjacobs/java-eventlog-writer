CREATE OR REPLACE FUNCTION article_facet_validate_article_config(p_model_sku text, p_article_config article_config, p_config_property_path text DEFAULT '')
  RETURNS constraint_violation_wrapper AS
$BODY$
-- $Id$
-- $HeadURL$
DECLARE
  l_violations constraint_violation_wrapper;
BEGIN

    l_violations.constraint_violations := '{}';

    -- check if season exists
    IF NOT EXISTS(
      select 1
        from zcat_commons.season
       where s_code = p_article_config.season_code
    ) THEN
      l_violations.constraint_violations := l_violations.constraint_violations ||
      (p_article_config.season_code, p_config_property_path || 'season_code', 'UNKNOWN_SEASON_CODE')::constraint_violation_value;
    END IF;

    -- check if first season exists
    IF NOT EXISTS(
      select 1
        from zcat_commons.season
       where s_code = p_article_config.first_season_code
    ) THEN
      l_violations.constraint_violations := l_violations.constraint_violations ||
      (p_article_config.first_season_code, p_config_property_path || 'first_season_code', 'UNKNOWN_SEASON_CODE')::constraint_violation_value;
    END IF;

    -- Check main color code exists
    IF NOT EXISTS (
      select null
        from zcat_commons.color
       where c_code = p_article_config.main_color_code
    ) THEN
      l_violations.constraint_violations := l_violations.constraint_violations ||
      (p_article_config.main_color_code, p_config_property_path || 'main_color_code', 'UNKNOWN_COLOR_CODE')::constraint_violation_value;
    END IF;

    -- Check main material color code exists
    IF p_article_config.main_material_code IS NOT NULL THEN
      IF NOT EXISTS (
        select null
          from zcat_commons.material
         where m_code = p_article_config.main_material_code
      ) THEN
        l_violations.constraint_violations := l_violations.constraint_violations ||
        (p_article_config.main_material_code, p_config_property_path || 'main_material_code', 'UNKNOWN_MATERIAL_CODE')::constraint_violation_value;
      END IF;
    END IF;

    -- Check second color code exists
    IF p_article_config.second_color_code IS NOT NULL THEN
      IF NOT EXISTS (
        select null
          from zcat_commons.color
         where c_code = p_article_config.second_color_code
      ) THEN
        l_violations.constraint_violations := l_violations.constraint_violations ||
        (p_article_config.second_color_code, p_config_property_path || 'second_color_code', 'UNKNOWN_COLOR_CODE')::constraint_violation_value;
      END IF;
    END IF;

    -- Third color code
    IF p_article_config.third_color_code IS NOT NULL THEN
      IF NOT EXISTS (
        select null
          from zcat_commons.color
         where c_code = p_article_config.third_color_code
      ) THEN
        l_violations.constraint_violations := l_violations.constraint_violations ||
        (p_article_config.third_color_code, p_config_property_path || 'third_color_code', 'UNKNOWN_COLOR_CODE')::constraint_violation_value;
      END IF;
    END IF;

    IF p_article_config.config_sku IS NULL AND p_model_sku IS NOT NULL THEN
        IF EXISTS(
            SELECT 1
            FROM zcat_data.sku_config_counter
            WHERE scc_model_sku = p_model_sku
                  AND ((p_article_config.main_color_code IS NULL AND scc_color_1 = 'N/A') OR (p_article_config.main_color_code = scc_color_1))
                  AND ((p_article_config.second_color_code IS NULL AND scc_color_2 = 'N/A') OR (p_article_config.second_color_code = scc_color_2))
                  AND ((p_article_config.third_color_code IS NULL AND scc_color_3 = 'N/A') OR (p_article_config.third_color_code = scc_color_3))
                  AND ((p_article_config.main_material_code IS NULL AND scc_material = 'N/A') OR (p_article_config.main_material_code = scc_material))
                  AND ((p_article_config.pattern_type_code IS NULL AND scc_pattern_code = 'N/A') OR ((p_article_config.pattern_type_code).code = scc_pattern_code))
        ) THEN
            l_violations.constraint_violations := l_violations.constraint_violations ||
                (p_article_config.main_color_code, p_config_property_path || 'main_color_code', 'DUPLICATED_COLORS_AND_MATERIAL_COMBINATION')::constraint_violation_value;
            l_violations.constraint_violations := l_violations.constraint_violations ||
                (p_article_config.second_color_code, p_config_property_path || 'second_color_code', 'DUPLICATED_COLORS_AND_MATERIAL_COMBINATION')::constraint_violation_value;
            l_violations.constraint_violations := l_violations.constraint_violations ||
                (p_article_config.third_color_code, p_config_property_path || 'third_color_code', 'DUPLICATED_COLORS_AND_MATERIAL_COMBINATION')::constraint_violation_value;
            l_violations.constraint_violations := l_violations.constraint_violations ||
                (p_article_config.main_material_code, p_config_property_path || 'main_material_code', 'DUPLICATED_COLORS_AND_MATERIAL_COMBINATION')::constraint_violation_value;
            l_violations.constraint_violations := l_violations.constraint_violations ||
                ((p_article_config.pattern_type_code).code, p_config_property_path || 'pattern_type_code', 'DUPLICATED_COLORS_AND_MATERIAL_COMBINATION')::constraint_violation_value;
        END IF;
    END IF;

    RETURN l_violations;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
