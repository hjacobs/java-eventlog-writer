CREATE OR REPLACE FUNCTION brand_get_brand(
  p_code varchar(3)
) RETURNS SETOF brand as
$$

  SELECT (b_code::varchar(3),
         b_name,
         b_is_own_brand)::brand
    FROM zcat_commons.brand
   WHERE b_code = $1;

$$
language SQL
STABLE security definer
cost 100;