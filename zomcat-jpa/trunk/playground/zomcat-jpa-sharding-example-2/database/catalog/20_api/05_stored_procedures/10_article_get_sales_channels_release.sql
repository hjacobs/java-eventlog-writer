CREATE OR REPLACE FUNCTION article_get_sales_channels_release (
    p_article_sku_id  int
) RETURNS sales_channels_release AS
$$

    SELECT ascr_version,
           ascr_is_lounge,
           ascr_is_offline_outlet,
           ascr_is_resale,
           ascr_is_emeza,
           ascr_is_kiomi
    FROM zcat_data.article_sales_channels_release
   WHERE ascr_article_sku_id = $1

$$
LANGUAGE SQL STABLE SECURITY DEFINER
COST 100;