CREATE OR REPLACE FUNCTION price_archive_get_price_history (
    p_simple_sku        text,
    p_start_date        timestamptz,
    p_end_date          timestamptz,
    p_app_domain_id     integer
) RETURNS SETOF price_history_entry AS
$BODY$

BEGIN

    RAISE INFO 'received simple_sku = %, start_date = %, end_date = %, app_domain_id = %',
        p_simple_sku,
        p_start_date,
        p_end_date,
        p_app_domain_id;

    RETURN QUERY
        SELECT as_sku               AS simple_sku,
               CASE WHEN pa_source_price_start_date > pa_source_promotional_price_start_date
                    THEN pa_source_price_start_date
                    ELSE pa_source_promotional_price_start_date
               END                  AS start_date,
               pa_created           AS end_date,
               pa_app_domain_id     AS app_domain_id,
               pa_price             AS price,
               pa_promotional_price AS promotional_price
          FROM zcat_data.price_archive
          JOIN zcat_data.article_sku ON as_id = pa_simple_sku_id AND as_sku_type = 'SIMPLE'
         WHERE as_sku = p_simple_sku
           AND pa_app_domain_id = p_app_domain_id
           AND pa_created BETWEEN p_start_date AND p_end_date
        UNION
        SELECT as_sku               AS simple_sku,
               pc_last_modified     AS start_date,
               p_end_date           AS end_date,
               pc_app_domain_id     AS app_domain_id,
               pc_price             AS price,
               pc_promotional_price AS promotional_price
          FROM zcat_data.price_current
          JOIN zcat_data.article_sku ON as_id = pc_simple_sku_id AND as_sku_type = 'SIMPLE'
         WHERE as_sku = p_simple_sku
           AND pc_app_domain_id = p_app_domain_id
           AND pc_created BETWEEN p_start_date AND p_end_date
           AND pc_price IS NOT NULL
        ORDER BY end_date;

END;
$BODY$

LANGUAGE plpgsql
  VOLATILE
  SECURITY DEFINER
  COST 100;