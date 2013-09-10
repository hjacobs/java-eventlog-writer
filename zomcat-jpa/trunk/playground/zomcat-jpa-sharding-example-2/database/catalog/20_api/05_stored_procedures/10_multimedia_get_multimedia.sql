CREATE OR REPLACE FUNCTION multimedia_get_multimedia (
    p_sku text
) RETURNS SETOF multimedia AS
    $BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN

    RETURN QUERY
        SELECT m_version,
               m_code,
               as_sku,
               m_type_code,
               m_is_external,
               m_path,
               m_media_character_code,
               m_checksum,
               m_width,
               m_height
          FROM zcat_data.multimedia
          JOIN zcat_data.article_sku ON as_id = m_sku_id
         WHERE as_sku = p_sku;

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
