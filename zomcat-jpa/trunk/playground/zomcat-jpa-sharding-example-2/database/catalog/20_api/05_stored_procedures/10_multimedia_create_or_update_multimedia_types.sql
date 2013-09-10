CREATE OR REPLACE FUNCTION multimedia_create_or_update_multimedia_type (
    p_multimedia_type  multimedia_type,
    p_scope            flow_scope
) RETURNS void AS
    $BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN

    BEGIN

        INSERT INTO zcat_commons.multimedia_type (
            mt_code,
            mt_created_by,
            mt_last_modified_by,
            mt_flow_id,
            mt_name,
            mt_mime_type,
            mt_is_active
        )
        VALUES (
            p_multimedia_type.multimedia_type_code,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_multimedia_type.name,
            p_multimedia_type.mime_type,
            p_multimedia_type.is_active);

    EXCEPTION
        WHEN unique_violation THEN

        UPDATE zcat_commons.multimedia_type
           SET
               mt_last_modified       = now(),
               mt_last_modified_by    = p_scope.user_id,
               mt_flow_id             = p_scope.flow_id,
               mt_version             = p_multimedia_type.version,

               mt_name        = p_multimedia_type.name,
               mt_mime_type   = p_multimedia_type.mime_type,
               mt_is_active   = p_multimedia_type.is_active
        WHERE mt_code = p_multimedia_type.multimedia_type_code;

    END;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
