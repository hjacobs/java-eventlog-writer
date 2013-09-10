CREATE OR REPLACE FUNCTION article_facet_multimedia_get_facets (
    text[],
    text
) returns setof multimedia_model as
$BODY$
/*
    $Id$
    $HeadURL$
*/
    with all_simples as (
        select distinct simple_id,
               config_id,
               model_id,
               simple_sku,
               config.as_sku as config_sku,
               model.as_sku as model_sku
          from (
            select  simples.as_id as simple_id,
                simples.as_config_id as config_id,
                simples.as_model_id as model_id,
                simples.as_sku as simple_sku
              from zcat_data.article_sku simples
              join zcat_data.article_sku top on top.as_id in (simples.as_id, simples.as_config_id, simples.as_model_id)
             where simples.as_sku_type = 'SIMPLE'
               and top.as_sku = ANY($1)

             union all

            select null as simple_id,
                   config.as_id as config_id,
                   config.as_model_id as model_id,
                   null as simple_sku
              from zcat_data.article_sku config
              join zcat_data.article_sku top on top.as_id in (config.as_id, config.as_model_id)
             where config.as_sku_type = 'CONFIG'
               and top.as_sku = ANY($1)
               and not exists (select 1 from zcat_data.article_sku where as_sku_type = 'SIMPLE' and top.as_id in (as_config_id, as_model_id) )

            union all

            select null as simple_id,
                   null as config_id,
                   model.as_id as model_id,
                   null as simple_sku
              from zcat_data.article_sku model
              join zcat_data.article_sku top on top.as_id = model.as_id
             where model.as_sku_type = 'MODEL'
               and top.as_sku = ANY($1)
               and not exists (select 1 from zcat_data.article_sku where as_sku_type = 'CONFIG' and top.as_id in (as_model_id) )
          ) t
          left join zcat_data.article_sku simple on simple.as_id = t.simple_id
          left join zcat_data.article_sku config on config.as_id = t.config_id
          left join zcat_data.article_sku model on model.as_id = t.model_id
    )

    select model_version,
           model_sku,
           array_agg(
               ROW(
                   (
                       mm_version,
                       mm_code,
                       model_sku,
                       mm_type_code,
                       mm_is_external,
                       mm_path,
                       mm_media_character_code,
                       mm_checksum,
                       mm_width,
                       mm_height
                   )::multimedia,
                   mfm_shop_frontend_type,
                   mfm_sort_key
               )::shop_multimedia
           )::shop_multimedia[] as model_shop_multimedia,
           nullif (array_agg (multimedia_config_data), ARRAY[null::multimedia_config])
    from (
        select
            case when config_sku is not null then
                ROW(
                    config_version,
                    config_sku,
                    array_agg(
                        ROW(
                            (
                                cm_version,
                                cm_code,
                                config_sku,
                                cm_type_code,
                                cm_is_external,
                                cm_path,
                                cm_media_character_code,
                                cm_checksum,
                                cm_width,
                                cm_height
                            )::multimedia,
                            mfc_shop_frontend_type,
                            mfc_sort_key
                        )::shop_multimedia
                    )::shop_multimedia[],
                    nullif (array_agg (multimedia_simple_data), ARRAY[null::multimedia_simple])
                )::multimedia_config
            else
                null::multimedia_config
            end as multimedia_config_data,
            model_version,
            model_sku,
            mm_version,
            mm_code,
            mm_type_code,
            mm_is_external,
            mm_path,
            mm_media_character_code,
            mm_checksum,
            mm_width,
            mm_height,
            mfm_shop_frontend_type,
            mfm_sort_key
        from (
            SELECT
                case when simple_sku is not null then
                    ROW(
                        multimedia_facet_simple.afm_version,
                        simple_sku,
                        array_agg(
                            ROW
                            (
                                (
                                    multimedia_simple.m_version,
                                    multimedia_simple.m_code,
                                    simple_sku,
                                    multimedia_simple.m_type_code,
                                    multimedia_simple.m_is_external,
                                    multimedia_simple.m_path,
                                    multimedia_simple.m_media_character_code,
                                    multimedia_simple.m_checksum,
                                    multimedia_simple.m_width,
                                    multimedia_simple.m_height
                                )::multimedia,
                                multimedia_facet_simple.afm_shop_frontend_type,
                                multimedia_facet_simple.afm_sort_key
                            )::shop_multimedia
                        )::shop_multimedia[]
                    )::multimedia_simple
                else
                    null::multimedia_simple
                end as multimedia_simple_data,
                multimedia_facet_model.afm_version model_version,
                model_sku,
                multimedia_model.m_version as mm_version,
                multimedia_model.m_code as mm_code,
                multimedia_model.m_type_code as mm_type_code,
                multimedia_model.m_is_external as mm_is_external,
                multimedia_model.m_path as mm_path,
                multimedia_model.m_media_character_code as mm_media_character_code,
                multimedia_model.m_checksum as mm_checksum,
                multimedia_model.m_width as mm_width,
                multimedia_model.m_height as mm_height,
                multimedia_facet_model.afm_shop_frontend_type as mfm_shop_frontend_type,
                multimedia_facet_model.afm_sort_key as mfm_sort_key,
                multimedia_facet_config.afm_version config_version,
                config_sku,
                multimedia_config.m_version as cm_version,
                multimedia_config.m_code as cm_code,
                multimedia_config.m_type_code as cm_type_code,
                multimedia_config.m_is_external as cm_is_external,
                multimedia_config.m_path as cm_path,
                multimedia_config.m_media_character_code as cm_media_character_code,
                multimedia_config.m_checksum as cm_checksum,
                multimedia_config.m_width as cm_width,
                multimedia_config.m_height as cm_height,
                multimedia_facet_config.afm_shop_frontend_type as mfc_shop_frontend_type,
                multimedia_facet_config.afm_sort_key as mfc_sort_key
            FROM all_simples sku
            join zcat_data.article_model on sku.model_id = article_model.am_model_sku_id
            left join zcat_data.article_config on sku.config_id = article_config.ac_config_sku_id
            left join zcat_data.article_simple on sku.simple_id = article_simple.as_simple_sku_id
            left join zcat_data.multimedia multimedia_model on sku.model_id = multimedia_model.m_sku_id
            left join zcat_data.multimedia multimedia_config on sku.config_id = multimedia_config.m_sku_id
            left join zcat_data.multimedia multimedia_simple on sku.simple_id = multimedia_simple.m_sku_id
            left join zcat_data.article_facet_multimedia multimedia_facet_model on multimedia_model.m_code = multimedia_facet_model.afm_code
            left join zcat_data.article_facet_multimedia multimedia_facet_config on multimedia_config.m_code = multimedia_facet_config.afm_code
            left join zcat_data.article_facet_multimedia multimedia_facet_simple on multimedia_simple.m_code = multimedia_facet_simple.afm_code
            group by
                multimedia_facet_model.afm_version,
                model_sku,
                multimedia_facet_config.afm_version,
                config_sku,
                multimedia_facet_simple.afm_version,
                simple_sku,
                multimedia_model.m_version,
                multimedia_model.m_code,
                multimedia_model.m_type_code,
                multimedia_model.m_is_external,
                multimedia_model.m_path,
                multimedia_model.m_media_character_code,
                multimedia_model.m_checksum,
                multimedia_model.m_width,
                multimedia_model.m_height,
                multimedia_facet_model.afm_shop_frontend_type,
                multimedia_facet_model.afm_sort_key,
                multimedia_config.m_version,
                multimedia_config.m_code,
                multimedia_config.m_type_code,
                multimedia_config.m_is_external,
                multimedia_config.m_path,
                multimedia_config.m_media_character_code,
                multimedia_config.m_checksum,
                multimedia_config.m_width,
                multimedia_config.m_height,
                multimedia_facet_config.afm_shop_frontend_type,
                multimedia_facet_config.afm_sort_key
        ) t
        group by
            model_version,
            model_sku,
            config_version,
            config_sku,
            mm_version,
            mm_code,
            mm_type_code,
            mm_is_external,
            mm_path,
            mm_media_character_code,
            mm_checksum,
            mm_width,
            mm_height,
            mfm_shop_frontend_type,
            mfm_sort_key

    ) r
    group by
        model_version,
        model_sku


$BODY$
language sql
volatile
security definer
cost 100;
