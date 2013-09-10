CREATE OR REPLACE FUNCTION multimedia_get_media_characters (
    p_include_inactive boolean
) RETURNS SETOF media_character AS
    $BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN

    RETURN QUERY
        SELECT mc_version,
               mc_code,
               mc_name,
               mc_is_active
          FROM zcat_commons.media_character
         WHERE (p_include_inactive = FALSE AND mc_is_active = TRUE) OR (p_include_inactive = TRUE);

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
