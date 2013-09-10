CREATE OR REPLACE FUNCTION color_get_by_code (
  p_color_code  text
) RETURNS setof color AS
$BODY$
BEGIN
  RETURN QUERY
     SELECT c_code,
            c_name_message_key,
            c_family_code
       FROM zcat_commons.color
       WHERE c_code = p_color_code;
END
$BODY$
language plpgsql
    STABLE security definer
    cost 100;
