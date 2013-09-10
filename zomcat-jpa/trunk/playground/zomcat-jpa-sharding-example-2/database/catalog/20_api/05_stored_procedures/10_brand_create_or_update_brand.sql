CREATE OR REPLACE FUNCTION brand_create_or_update_brand(
  p_brand brand,
  p_scope flow_scope
)
returns void as
$BODY$
DECLARE
  l_code varchar(3);
  l_name text;
  l_is_own_brand boolean;
  l_update boolean;
BEGIN

  FOR l_code, l_name, l_is_own_brand IN
    SELECT b_code,
           b_name,
           b_is_own_brand
      FROM zcat_commons.brand
     WHERE b_code = p_brand.brand_code
        OR b_name = p_brand.name
  LOOP
    IF l_name = p_brand.name AND l_code != p_brand.brand_code THEN
      RAISE EXCEPTION 'brand with name "%" already exists with code %', p_brand.name , l_code;
    ELSIF l_name = p_brand.name AND l_code = p_brand.brand_code AND l_is_own_brand = p_brand.own_brand THEN
      l_update = false;
      EXIT;
    ELSE
      l_update = true;
      EXIT;
    END IF;
  END LOOP;

  IF NOT FOUND THEN
    INSERT
      INTO zcat_commons.brand (
             b_code,
             b_name,
             b_is_own_brand,
             b_created_by,
             b_last_modified_by,
             b_flow_id
           )
    VALUES (
             p_brand.brand_code,
             p_brand.name,
             p_brand.own_brand,
             p_scope.user_id,
             p_scope.user_id,
             p_scope.flow_id
           );
  END IF;

  IF l_update THEN
    UPDATE zcat_commons.brand
       SET b_name               = p_brand.name,
           b_is_own_brand       = p_brand.own_brand,
           b_last_modified      = now(),
           b_last_modified_by   = p_scope.user_id,
           b_flow_id            = p_scope.flow_id
     WHERE b_code = p_brand.brand_code;
  END IF;

END
$BODY$
language plpgsql
    volatile security definer
    cost 100;