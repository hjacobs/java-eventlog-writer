CREATE OR REPLACE FUNCTION migrate_article(
    p_model_sku text,
    p_configs config_sku_hierarchy[],
    p_scope flow_scope
)
  RETURNS SETOF text AS
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/* --test
begin;
 SELECT * FROM migrate_article('Z5OMRMGe0','{"(Z5OMRMGe0-612,\"{Z5OMRMGe0-612WPcC4eW,Z5OMRMGe0-6122dZy9V8}\")","(Z5OMRMGe0-735,\"{Z5OMRMGe0-7353dSNKkV,Z5OMRMGe0-735Mrgrj76,Z5OMRMGe0-73512i6Ff3}\")","(Z5OMRMGe0-400,\"{Z5OMRMGe0-40034ZTMD9,Z5OMRMGe0-400Z1JX6ML,Z5OMRMGe0-400G215E6R}\")","(Z5OMRMGe0-761,\"{Z5OMRMGe0-761OG7KEH3,Z5OMRMGe0-761Hm3BLdJ}\")"}');
rollback;
 */
DECLARE
  l_model_id int;
  l_config_id int;
  l_simple_id int;
  l_idx1 int;
  l_idx2 int;
  l_flag boolean;
  l_result text[];
BEGIN
  RAISE INFO 'called migrate article sku = %, configs = %, database = %', p_model_sku, p_configs, current_database();

  l_result := ARRAY[]::text[];
  SELECT as_id
    INTO l_model_id
    FROM zcat_data.article_sku
   WHERE as_sku = p_model_sku
     AND as_sku_type = 'MODEL';

  IF NOT FOUND THEN
    l_model_id := article_create_sku (p_model_sku, null, null, 'MODEL', p_scope);
    l_result := array_append(l_result, p_model_sku);
  END IF;

  FOR l_idx1 IN 1..COALESCE(array_upper(p_configs,1),0) LOOP
    RAISE INFO 'config %',p_configs[l_idx1];
    SELECT as_id
      INTO l_config_id
      FROM zcat_data.article_sku
     WHERE as_sku = p_configs[l_idx1].sku
       AND as_sku_type = 'CONFIG';

    IF NOT FOUND THEN
        l_config_id := article_create_sku (p_configs[l_idx1].sku, l_model_id, null, 'CONFIG', p_scope);
        l_result := array_append(l_result, p_configs[l_idx1].sku);
    END IF;

    FOR l_idx2 IN 1..COALESCE(array_upper(p_configs[l_idx1].simples,1),0) LOOP
      RAISE INFO '  simple %',p_configs[l_idx1].simples[l_idx2];
      SELECT as_id
        INTO l_simple_id
        FROM zcat_data.article_sku
       WHERE as_sku = p_configs[l_idx1].simples[l_idx2]
         AND as_sku_type = 'SIMPLE';

      IF NOT FOUND THEN
        perform article_create_sku (p_configs[l_idx1].simples[l_idx2], l_model_id, l_config_id, 'SIMPLE', p_scope);
        l_result := array_append(l_result, p_configs[l_idx1].simples[l_idx2]);
      END IF;
    END LOOP;
  END LOOP;

  RETURN QUERY SELECT UNNEST(l_result);

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
