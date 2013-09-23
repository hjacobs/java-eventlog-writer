CREATE OR REPLACE FUNCTION article_get_active_ean (
  p_simple_sku_id  int
) RETURNS setof text AS
$$

  SELECT ase_ean::text
    FROM zcat_data.article_simple_ean
   WHERE ase_simple_sku_id = $1
     AND ase_is_active = TRUE
   ORDER BY ase_valid_from DESC

$$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
