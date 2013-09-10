CREATE OR REPLACE FUNCTION brand_get_brands()
returns SETOF brand as
$BODY$
BEGIN
  RETURN QUERY
    SELECT b_code,
           b_name,
           b_is_own_brand
      FROM zcat_commons.brand;
END
$BODY$
language plpgsql
    STABLE security definer
    cost 100;