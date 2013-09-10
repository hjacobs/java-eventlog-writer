CREATE OR REPLACE FUNCTION article_get_sku_id(
  p_sku       text,
  p_sku_type  zcat_data.sku_type
) RETURNS int AS
$BODY$
DECLARE
  l_sku_id int;
BEGIN

  SELECT as_id
    INTO l_sku_id
    FROM zcat_data.article_sku
   WHERE as_sku = p_sku
     AND as_sku_type = p_sku_type;

  IF NOT FOUND THEN
    RAISE EXCEPTION 'Sku "%" of type % does not exist', p_sku, p_sku_type;
  END IF;

  RETURN l_sku_id;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;