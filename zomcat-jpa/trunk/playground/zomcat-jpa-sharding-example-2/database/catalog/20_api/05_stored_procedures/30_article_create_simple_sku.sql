CREATE OR REPLACE FUNCTION article_create_simple_sku(
    p_model_sku text,
    p_config_sku text,
    p_simple_sku text,
    p_scope flow_scope
) RETURNS int AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test

  set search_path=zcat_api_r12_00_40;
  SELECT * FROM article_create_simple_sku ('10K11A008', '10K11A008-102','10K11A008-1020420000');
*/

DECLARE
  l_model_id int;
  l_config_id int;
  l_simple_id int;
BEGIN
  RAISE INFO 'called create_simple_sku model_sku = %, config_sku = %, config_sku = %, database = %',
    p_model_sku,
    p_config_sku,
    p_simple_sku,
    current_database();

  -- check for a substring at the beginning:
  IF position(p_config_sku in p_simple_sku) <> 1 THEN
    RAISE EXCEPTION 'config sku % is not a substring of simple sku %', p_config_sku, p_simple_sku;
  END IF;

  SELECT as_id INTO l_model_id
  FROM zcat_data.article_sku
  where as_sku = p_model_sku and as_sku_type = 'MODEL';

  IF l_model_id IS NULL THEN
    RAISE INFO 'creating model sku';
    l_model_id := article_create_sku (p_model_sku, null, null, 'MODEL', p_scope);
  END IF;

  SELECT as_id INTO l_config_id
  FROM zcat_data.article_sku
  WHERE as_sku = p_config_sku AND as_model_id = l_model_id AND as_sku_type = 'CONFIG';

  IF l_config_id IS NULL THEN
    RAISE INFO 'creating config sku';
    l_config_id := article_create_sku (p_config_sku, l_model_id, null, 'CONFIG', p_scope);
  END IF;

  RAISE INFO 'using model id= %, config id= %, database = %', l_model_id, l_config_id, current_database();

  SELECT as_id INTO l_simple_id
  FROM zcat_data.article_sku
  WHERE as_sku = p_simple_sku AND as_model_id = l_model_id AND as_config_id = l_config_id AND as_sku_type = 'SIMPLE';

  IF l_simple_id IS NULL THEN
    RAISE INFO 'creating simple sku';
    l_simple_id := article_create_sku (p_simple_sku, l_model_id, l_config_id, 'SIMPLE', p_scope);
  END IF;

  RETURN l_simple_id;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
