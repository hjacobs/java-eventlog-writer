CREATE OR REPLACE FUNCTION article_create_model_sku(
    p_model_sku text,
    p_scope flow_scope
)
  RETURNS int AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test

  set search_path=zcat_api_r12_00_40;
  SELECT * FROM create_model_sku ('10K11A008');
*/
DECLARE
  l_model_id int;
BEGIN
  RAISE INFO 'called create_model_sku sku = %, database = %', p_model_sku, current_database();

  SELECT as_id INTO l_model_id
  FROM zcat_data.article_sku
  WHERE as_sku = p_model_sku AND as_sku_type = 'MODEL';

  IF l_model_id IS NULL THEN
    RAISE INFO 'creating model sku';
    l_model_id := article_create_sku (p_model_sku, null, null, 'MODEL', p_scope);
  END IF;

  RETURN l_model_id;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
