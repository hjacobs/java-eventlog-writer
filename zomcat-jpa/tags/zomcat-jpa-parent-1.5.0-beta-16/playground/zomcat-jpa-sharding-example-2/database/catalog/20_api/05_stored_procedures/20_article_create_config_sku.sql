CREATE OR REPLACE FUNCTION article_create_config_sku(
    p_model_sku text,
    p_config_sku text,
    p_scope flow_scope
)
  RETURNS int AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test
  set search_path=zcat_api_r12_00_40;
  SELECT * FROM article_create_config_sku ('10K11A008', '10K11A008-102');
*/

DECLARE
  l_model_id int;
  l_config_id int;
BEGIN
  RAISE INFO 'called create_config_sku model_sku = %, config_sku = %, database = %',
    p_model_sku,
    p_config_sku,
    current_database();

  -- check for a substring at the beginning:
  IF position(p_model_sku in p_config_sku) <> 1 THEN
    RAISE EXCEPTION 'model sku % is not a substring of config sku %', p_model_sku, p_config_sku;
  END IF;

  SELECT as_id INTO l_model_id
  FROM zcat_data.article_sku
  WHERE as_sku = p_model_sku AND as_sku_type = 'MODEL';

  IF l_model_id IS NULL THEN
    RAISE INFO 'creating model';
    l_model_id := article_create_sku (p_model_sku, null, null, 'MODEL', p_scope);
  END IF;

  SELECT as_id INTO l_config_id
  FROM zcat_data.article_sku
  WHERE as_sku = p_config_sku AND as_model_id = l_model_id AND as_sku_type = 'CONFIG';

  IF l_config_id IS NULL THEN
    RAISE INFO 'creating config sku';
    l_config_id := article_create_sku (p_config_sku, l_model_id, null, 'CONFIG', p_scope);
  END IF;

  RETURN l_config_id;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
