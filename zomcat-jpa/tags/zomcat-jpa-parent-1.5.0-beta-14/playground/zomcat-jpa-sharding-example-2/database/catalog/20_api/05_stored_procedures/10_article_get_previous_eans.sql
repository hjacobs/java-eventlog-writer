CREATE OR REPLACE FUNCTION article_get_previous_eans (
  p_simple_sku_id  int
) RETURNS SETOF text AS
$$

  SELECT ase_ean::text
    FROM zcat_data.article_simple_ean
   WHERE ase_simple_sku_id = $1
     AND ase_is_active = FALSE
   ORDER BY ase_valid_from

$$
LANGUAGE SQL VOLATILE SECURITY DEFINER
COST 100;
