CREATE OR REPLACE FUNCTION multimedia_update_multimedia (
    p_multimedia multimedia,
    p_scope      flow_scope
) RETURNS VOID AS
    $BODY$
/*
-- $Id$
-- $HeadURL$
*/
DECLARE
    l_sku_id int;
    l_sku_type zcat_data.sku_type;
BEGIN

    SELECT as_id, as_sku_type
      INTO l_sku_id, l_sku_type
      FROM zcat_data.article_sku
     WHERE as_sku = p_multimedia.sku
     ORDER BY as_sku_type
     LIMIT 1;

    IF NOT FOUND THEN
      RAISE EXCEPTION 'sku % not found', p_multimedia.sku USING ERRCODE = 'Z0001';
    END IF;

    IF l_sku_type = 'MODEL' THEN

        IF NOT EXISTS(SELECT 1 FROM zcat_data.article_model WHERE am_model_sku_id = l_sku_id) THEN
            RAISE EXCEPTION 'Article model with sku % not found.', p_multimedia.sku USING ERRCODE = 'Z0005';
        END IF;

    ELSIF l_sku_type = 'CONFIG' THEN

        IF NOT EXISTS(SELECT 1 FROM zcat_data.article_config WHERE ac_config_sku_id = l_sku_id) THEN
            RAISE EXCEPTION 'Article config with sku % not found.', p_multimedia.sku USING ERRCODE = 'Z0003';
        END IF;

    ELSE

        IF NOT EXISTS(SELECT 1 FROM zcat_data.article_simple WHERE as_simple_sku_id = l_sku_id) THEN
            RAISE EXCEPTION 'Article simple with sku % not found.', p_multimedia.sku USING ERRCODE = 'Z0004';
        END IF;

    END IF;

    UPDATE zcat_data.multimedia
       SET
           m_last_modified = now(),
           m_last_modified_by = p_scope.user_id,
           m_flow_id = p_scope.user_id,

           m_type_code = p_multimedia.type_code,
           m_is_external = p_multimedia.is_external,
           m_path = p_multimedia.path,
           m_media_character_code = p_multimedia.media_character_code,
           m_checksum = p_multimedia.checksum,
           m_width = p_multimedia.width,
           m_height = p_multimedia.height
    WHERE m_code = p_multimedia.code;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
