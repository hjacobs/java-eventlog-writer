CREATE OR REPLACE FUNCTION article_facet_multimedia_create_or_update(
    p_multimedia_facet_wrapper  multimedia_facet_wrapper,
    p_scope               flow_scope
) RETURNS text AS
$BODY$
/*
    $Id$
    $HeadURL$

    Creates or updates a multimedia facet model/config/simple.

*/
DECLARE
    l_sku_id int;
    l_shop_multimedia shop_multimedia;
    l_shop_multimedia_array shop_multimedia[];
BEGIN

    -- determine sku id
    IF (p_multimedia_facet_wrapper.multimedia_model).model_sku IS NOT NULL THEN

        SELECT article_get_sku_id((p_multimedia_facet_wrapper.multimedia_model).model_sku, 'MODEL') INTO l_sku_id;

        IF NOT EXISTS(SELECT 1 FROM zcat_data.article_model WHERE am_model_sku_id = l_sku_id) THEN
            RAISE EXCEPTION 'Article model with sku % not found.', (p_multimedia_facet_wrapper.multimedia_model).model_sku USING ERRCODE = 'Z0005';
        END IF;

        l_shop_multimedia_array := (p_multimedia_facet_wrapper.multimedia_model).shop_multimedia;

    ELSIF (p_multimedia_facet_wrapper.multimedia_config).config_sku IS NOT NULL THEN

        SELECT article_get_sku_id((p_multimedia_facet_wrapper.multimedia_config).config_sku, 'CONFIG') INTO l_sku_id;

        IF NOT EXISTS(SELECT 1 FROM zcat_data.article_config WHERE ac_config_sku_id = l_sku_id) THEN
            RAISE EXCEPTION 'Article config with sku % not found.', (p_multimedia_facet_wrapper.multimedia_config).config_sku USING ERRCODE = 'Z0003';
        END IF;

        l_shop_multimedia_array := (p_multimedia_facet_wrapper.multimedia_config).shop_multimedia;

    ELSIF (p_multimedia_facet_wrapper.multimedia_simple).simple_sku IS NOT NULL THEN

        SELECT article_get_sku_id((p_multimedia_facet_wrapper.multimedia_simple).simple_sku, 'SIMPLE') INTO l_sku_id;

        IF NOT EXISTS(SELECT 1 FROM zcat_data.article_simple WHERE as_simple_sku_id = l_sku_id) THEN
            RAISE EXCEPTION 'Article simple with sku % not found.', (p_multimedia_facet_wrapper.multimedia_simple).simple_sku USING ERRCODE = 'Z0004';
        END IF;

        l_shop_multimedia_array := (p_multimedia_facet_wrapper.multimedia_simple).shop_multimedia;

    ELSE
        RAISE EXCEPTION 'Wrapper parameter not set correctly. None of facets is set: model: %, config: %, simple: %', p_multimedia_facet_wrapper.multimedia_model, p_multimedia_facet_wrapper.multimedia_config, p_multimedia_facet_wrapper.multimedia_simple;
    END IF;


    FOREACH l_shop_multimedia IN ARRAY l_shop_multimedia_array LOOP

        PERFORM 1
          FROM zcat_data.multimedia
          WHERE m_code = (l_shop_multimedia.multimedia).code AND m_sku_id = l_sku_id;

        IF NOT FOUND THEN
            RAISE EXCEPTION 'Multimedia entry with code % for sku: % not found.', (l_shop_multimedia.multimedia).code,  (l_shop_multimedia.multimedia).sku USING ERRCODE = 'Z0006';
        END IF;

        -- create or update multimedia facet
        BEGIN

            INSERT INTO zcat_data.article_facet_multimedia (
                afm_code,
                afm_shop_frontend_type,
                afm_created_by,
                afm_last_modified_by,
                afm_flow_id,
                afm_sort_key
            )
            VALUES (
                (l_shop_multimedia.multimedia).code,
                l_shop_multimedia.shop_frontend_type,
                p_scope.user_id,
                p_scope.user_id,
                p_scope.flow_id,
                l_shop_multimedia.sort_key
            );

        EXCEPTION
            WHEN unique_violation THEN

            UPDATE zcat_data.article_facet_multimedia
               SET
                   afm_last_modified = now(),
                   afm_last_modified_by = p_scope.user_id,
                   afm_flow_id = p_scope.flow_id,
                   afm_version = COALESCE((p_multimedia_facet_wrapper.multimedia_model).version,
                                          (p_multimedia_facet_wrapper.multimedia_config).version,
                                          (p_multimedia_facet_wrapper.multimedia_simple).version),
                   afm_sort_key = l_shop_multimedia.sort_key
             WHERE afm_code = (l_shop_multimedia.multimedia).code
               AND afm_shop_frontend_type = l_shop_multimedia.shop_frontend_type;

        END;
    END LOOP;

    RETURN 'UPDATE'; -- not sure which status to return as it could create and updates entries in one query

END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;
