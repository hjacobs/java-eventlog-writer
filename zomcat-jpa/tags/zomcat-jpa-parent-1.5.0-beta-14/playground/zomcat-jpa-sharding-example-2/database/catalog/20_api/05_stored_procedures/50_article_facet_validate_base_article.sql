CREATE OR REPLACE FUNCTION article_facet_validate_base_article(p_article_model article_model)
  RETURNS constraint_violation_wrapper AS
$BODY$
-- $Id$
-- $HeadURL$
DECLARE
    l_violations constraint_violation_wrapper;
    l_article_config article_config;
    l_article_simple article_simple;
    l_config_index int := 0;
    l_simple_index int := 0;
    l_config_property_path text;
    l_simple_property_path text;
BEGIN

    l_config_property_path := '';
    l_violations.constraint_violations := '{}';

    l_violations.constraint_violations := l_violations.constraint_violations || (article_facet_validate_article_model(p_article_model)).constraint_violations;

    FOREACH l_article_config IN ARRAY(p_article_model.config_facets) LOOP
        l_config_property_path := 'config[' || l_config_index || '].';

        FOREACH l_article_simple IN ARRAY(l_article_config.simple_facets) LOOP
            l_simple_property_path := l_config_property_path || '.simple[' || l_simple_index || '].';
            l_violations.constraint_violations := l_violations.constraint_violations || (article_facet_validate_article_simple(null, l_article_simple, l_simple_property_path)).constraint_violations;
            l_simple_index = l_simple_index + 1;
        END LOOP;

        l_violations.constraint_violations := l_violations.constraint_violations || (article_facet_validate_article_config(null, l_article_config, l_config_property_path)).constraint_violations;

        l_config_index = l_config_index + 1;
    END LOOP;

    RETURN l_violations;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
