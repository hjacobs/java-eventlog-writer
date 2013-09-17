CREATE OR REPLACE FUNCTION multimedia_get_multimedia_types (
    p_include_inactive boolean
) RETURNS SETOF multimedia_type AS
    $BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN

    RETURN QUERY
        SELECT mt_version,
               mt_code,
               mt_name,
               mt_mime_type,
               mt_is_active
          FROM zcat_commons.multimedia_type
         WHERE (p_include_inactive = FALSE AND mt_is_active = TRUE) OR (p_include_inactive = TRUE);

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
