CREATE OR REPLACE FUNCTION multimedia_create_multimedia (
    p_multimedia multimedia,
    p_virtual_shard_id int,
    p_scope      flow_scope
) RETURNS bigint AS
    $BODY$
/*
-- $Id$
-- $HeadURL$
*/
DECLARE
    l_sku_id int;
    l_sku_type zcat_data.sku_type;
    l_code bigint;
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

    INSERT INTO zcat_data.multimedia(
        m_code,

        m_created_by,
        m_last_modified_by,
        m_flow_id,

        m_sku_id,
        m_type_code,
        m_is_external,
        m_path,
        m_media_character_code,
        m_checksum,
        m_width,
        m_height)
    values (
        (select get_shard_aware_id('MULTIMEDIA', p_virtual_shard_id)),

        p_scope.user_id,
        p_scope.user_id,
        p_scope.flow_id,

        l_sku_id,
        p_multimedia.type_code,
        p_multimedia.is_external,
        p_multimedia.path,
        p_multimedia.media_character_code,
        p_multimedia.checksum,
        p_multimedia.width,
        p_multimedia.height
    )
    RETURNING m_code INTO l_code;

    RETURN l_code;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
