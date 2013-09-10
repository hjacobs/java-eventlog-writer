CREATE OR REPLACE FUNCTION multimedia_create_or_update_media_character (
    p_media_character  media_character,
    p_scope            flow_scope
) RETURNS void AS
    $BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN

    BEGIN

        INSERT INTO zcat_commons.media_character (
            mc_code,
            mc_created_by,
            mc_last_modified_by,
            mc_flow_id,
            mc_name,
            mc_is_active
        )
        VALUES (
            p_media_character.media_character_code,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_media_character.name,
            p_media_character.is_active);

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_commons.media_character
           SET
               mc_last_modified       = now(),
               mc_last_modified_by    = p_scope.user_id,
               mc_flow_id             = p_scope.flow_id,
               mc_version             = p_media_character.version,

               mc_name        = p_media_character.name,
               mc_is_active   = p_media_character.is_active
        WHERE mc_code = p_media_character.media_character_code;

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
