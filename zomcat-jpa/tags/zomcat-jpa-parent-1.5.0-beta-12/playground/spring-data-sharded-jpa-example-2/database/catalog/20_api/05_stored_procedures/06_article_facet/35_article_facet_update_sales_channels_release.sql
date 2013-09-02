CREATE OR REPLACE FUNCTION article_facet_update_sales_channels_release(
  p_article_sku_id          integer,
  p_sales_channels_release  sales_channels_release,
  p_scope                   flow_scope
) RETURNS void AS
$BODY$
BEGIN

    BEGIN

        INSERT INTO zcat_data.article_sales_channels_release(
           ascr_article_sku_id,

           ascr_created_by,
           ascr_last_modified_by,
           ascr_flow_id,

           ascr_is_lounge,
           ascr_is_offline_outlet,
           ascr_is_resale,
           ascr_is_emeza,
           ascr_is_kiomi)
        VALUES (p_article_sku_id,

                p_scope.user_id,
                p_scope.user_id,
                p_scope.flow_id,

                p_sales_channels_release.lounge,
                p_sales_channels_release.offline_outlet,
                p_sales_channels_release.resale,
                p_sales_channels_release.emeza,
                p_sales_channels_release.kiomi);

    EXCEPTION
    WHEN unique_violation THEN

        UPDATE zcat_data.article_sales_channels_release
        SET ascr_is_lounge         = p_sales_channels_release.lounge,
            ascr_is_offline_outlet = p_sales_channels_release.offline_outlet,
            ascr_is_resale         = p_sales_channels_release.resale,
            ascr_is_emeza          = p_sales_channels_release.emeza,
            ascr_is_kiomi          = p_sales_channels_release.kiomi,
            ascr_version           = p_sales_channels_release.version,

            ascr_last_modified       = now(),
            ascr_last_modified_by    = p_scope.user_id,
            ascr_flow_id             = p_scope.flow_id
        WHERE ascr_article_sku_id = p_article_sku_id;

    END;

    RETURN;
END;
$BODY$
LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
COST 100;