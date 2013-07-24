CREATE OR REPLACE function price_conf_mgmt_create_or_update_price_definition (
    p_price_definition     price_definition,
    p_scope                flow_scope,
    p_original_pd_id       bigint = null,
    p_is_overwrite         boolean = false,
    p_is_high_priority     boolean = false
) returns bigint as
$BODY$
/*
    -- $Id$
    -- $HeadURL$

show search_path
set search_path to zcat_api_r12_00_37, public

*/
/** -- test
show search_path;

SELECT * FROM price_conf_mgmt_create_or_update_price_definition ( ROW(null, 'sku', ROW(1, 150, 'normal')::price_level, 100, 1, '2012-01-01', '2012-02-01', null, false, null)::price_definition, null, null, false, false )
SELECT * FROM price_conf_mgmt_create_or_update_price_definition ( ROW(null, 'sku', ROW(1, 150, 'normal')::price_level, 100, 1, '2012-01-01', '2012-02-01', null, false, 3001)::price_definition, null, null, false, false )

*/
DECLARE
    l_price_level_id                smallint;
    l_price_level_compare_id        smallint;
    l_price_level_reason_code_id    smallint;
    l_price_definition_id           bigint;
    l_sku_id                        bigint;

BEGIN

    -- first we resolve the sku to an id
    -- NOTE: legacy articles have the same sku for MODEL and CONFIG !
    -- it does not really matter on which level we set the price so we order the result
    -- and take the first (which will be the CONFIG level)
    SELECT as_id
      INTO l_sku_id
      FROM zcat_data.article_sku
     WHERE as_sku = p_price_definition.sku
     ORDER BY as_sku_type
     LIMIT 1;

    IF NOT FOUND THEN
      raise exception 'sku % not found', p_price_definition.sku USING ERRCODE = 'Z0001';
    END IF;

    -- check price_level
    SELECT pl_id
      INTO l_price_level_id
      FROM zcat_commons.price_level
     WHERE pl_level = (p_price_definition.price_level).level;

    IF NOT FOUND THEN
        raise exception 'price level not found';
    END IF;

    -- get price level reason

    RAISE INFO 'p_price_definition: %', p_price_definition;

    IF (p_price_definition.price_level_reason_code).value IS NOT NULL THEN
        RAISE INFO 'price_level_reason_code: %', (p_price_definition.price_level_reason_code).value;
        SELECT price_conf_mgmt_get_valid_plrc_id((p_price_definition.price_level_reason_code).value)
          INTO l_price_level_reason_code_id;
    ELSE
        RAISE INFO 'no price_level_reason_code found!';
        l_price_level_reason_code_id = null;
    END IF;

    p_price_definition.start_date := date_trunc ('second', p_price_definition.start_date);
    p_price_definition.end_date := date_trunc ('second', p_price_definition.end_date);

    IF p_price_definition.id IS NULL THEN

        IF NOT p_is_overwrite THEN

            perform 1
             FROM zcat_data.price_definition
            WHERE pd_sku_id = l_sku_id
              AND pd_price_level_id = l_price_level_id
              AND pd_appdomain_id IS NOT DISTINCT FROM p_price_definition.appdomain_id
              AND p_price_definition.country_code = pd_country_code
              AND pd_partner_id IS NOT DISTINCT FROM p_price_definition.partner_id
              AND ( ( (p_price_definition.start_date, p_price_definition.end_date) OVERLAPS (date_trunc('second', pd_start_date), date_trunc('second', pd_end_date)) ) OR
                    ( (date_trunc('second', pd_start_date), date_trunc('second', pd_end_date)) OVERLAPS (p_price_definition.start_date, p_price_definition.end_date) ) OR
                    ( p_price_definition.start_date < date_trunc('second', pd_start_date) AND p_price_definition.end_date = date_trunc('second', pd_start_date) )      OR
                    ( p_price_definition.start_date = date_trunc('second', pd_end_date) AND p_price_definition.end_date > date_trunc('second', pd_end_date) )
                  );

            IF FOUND THEN
                -- not: if changing the following message , adjust the PriceMigrationWebServiceImpl class
                -- which depends on 'inserting price overlaps' as a marker
                raise exception 'inserting price overlaps. not inserting! sku: % pl: % ad: % partner: % start: % end: %', p_price_definition.sku, l_price_level_id, p_price_definition.appdomain_id,
                    p_price_definition.partner_id, p_price_definition.start_date, p_price_definition.end_date;
            END IF;

        END IF;

        INSERT INTO zcat_data.price_definition(
            pd_sku_id,
            pd_price_level_id,
            pd_price,
            pd_appdomain_id,
            pd_country_code,
            pd_start_date,
            pd_end_date,
            pd_partner_id,
            pd_price_level_reason_code_id
        )
        VALUES (
            l_sku_id,
            l_price_level_id,
            p_price_definition.price,
            p_price_definition.appdomain_id,
            p_price_definition.country_code,
            p_price_definition.start_date,
            p_price_definition.end_date,
            p_price_definition.partner_id,
            l_price_level_reason_code_id
        )
        RETURNING pd_id INTO l_price_definition_id;

        INSERT INTO zcat_data.price_definition_additional_info(
            pdai_price_definition_id,
            pdai_created_by,
            pdai_last_modified_by,
            pdai_flow_id,
            pdai_is_high_priority,
            pdai_original_price_definition_id
        )
        VALUES (
            l_price_definition_id,
            p_scope.user_id,
            p_scope.user_id,
            p_scope.flow_id,
            p_is_high_priority,
            p_original_pd_id
        );

    ELSE

        UPDATE zcat_data.price_definition
           SET pd_sku_id = l_sku_id,
               pd_price_level_id = l_price_level_id,
               pd_price = p_price_definition.price,
               pd_appdomain_id = p_price_definition.appdomain_id,
               pd_country_code = p_price_definition.country_code,
               pd_start_date = p_price_definition.start_date,
               pd_end_date = p_price_definition.end_date,
               pd_price_level_reason_code_id = l_price_level_reason_code_id
         WHERE pd_id = p_price_definition.id
        RETURNING pd_id INTO l_price_definition_id;

        UPDATE zcat_data.price_definition_additional_info
           SET pdai_last_modified_by = p_scope.user_id,
               pdai_last_modified = now(),
               pdai_flow_id = p_scope.flow_id,
               pdai_is_high_priority = p_is_high_priority
         WHERE pdai_price_definition_id = l_price_definition_id;

        -- update binded base fallback price definition
        UPDATE zcat_data.price_definition
           SET pd_start_date = p_price_definition.start_date,
               pd_end_date = p_price_definition.end_date
          FROM zcat_data.price_definition_additional_info
         WHERE pd_id = pdai_price_definition_id
           AND pdai_original_price_definition_id = l_price_definition_id;

    END IF;

    RETURN l_price_definition_id;
END
$BODY$

language plpgsql
  volatile
  security definer
  cost 100;
