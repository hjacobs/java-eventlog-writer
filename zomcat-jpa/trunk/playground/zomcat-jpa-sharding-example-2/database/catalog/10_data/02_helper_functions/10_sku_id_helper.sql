
CREATE OR REPLACE FUNCTION zcat_data.is_model_sku_id (p_sku_id integer) RETURNS boolean AS $$
BEGIN
  RETURN p_sku_id IS NOT NULL AND p_sku_id > 0 AND p_sku_id % 2 = 1;
END;
$$ LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION zcat_data.is_config_sku_id (p_sku_id integer) RETURNS boolean AS $$
BEGIN
  RETURN p_sku_id IS NOT NULL AND p_sku_id > 0 AND p_sku_id % 2 = 0;
END;
$$ LANGUAGE 'plpgsql';


CREATE OR REPLACE FUNCTION zcat_data.is_simple_sku_id (p_sku_id integer) RETURNS boolean AS $$
BEGIN
  RETURN p_sku_id IS NOT NULL AND p_sku_id < 0;
END;
$$ LANGUAGE 'plpgsql';