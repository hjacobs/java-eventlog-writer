CREATE OR REPLACE FUNCTION price_archive_add_entries(
    price_archive_entry[]
)
RETURNS SETOF price_archive_entry AS $$

/**
 * Adds new entries to price_current table if no similar entry exists (simple_sku_id, app_domain_id, partner_id).
 * If similar entry exists but have different values, then moves entry to price_archive table
 * and updates price_current table with new values.
 *
 * @ExpectedExecutionTime 20ms
 * @ExpectedExecutionFrequency Bulk PriceChangeEvent
 */
/* --testing

  begin;
    set search_path to zcat_api, public;
    set client_min_messages to debug1;

    SELECT * FROM article_create_simple_sku ('XXXXXXXXX', 'XXXXXXXXX-000','XXXXXXXXX-000000S000');
    SELECT * FROM article_create_simple_sku ('XXXXXXXXX', 'XXXXXXXXX-000','XXXXXXXXX-000000M000');
    SELECT * FROM article_create_simple_sku ('XXXXXXXXX', 'XXXXXXXXX-000','XXXXXXXXX-000000L000');

    SELECT zcat_api.price_archive_add_entries(
        ARRAY[
          ('XXXXXXXXX-000000X000', 1, null, 11, 9, null, null, null, null, null, null, null, null, null, null, null, null, null),
          ('XXXXXXXXX-000000S000', 1, null, 11, 9, null, null, null, null, null, null, null, null, null, null, null, null, null),
          ('XXXXXXXXX-000000M000', 1, null, 10, 9, null, null, null, null, null, null, null, null, null, null, null, null, null),
          ('XXXXXXXXX-000000L000', 1, null, 11, 10, null, null, null, null, null, null, null, null, null, null, null, null, null),
          ('XXXXXXXXX-000000Y000', 1, null, 11, 9, null, null, null, null, null, null, null, null, null, null, null, null, null)

        ]:: price_archive_entry[]
    );

  rollback;

  -- result returns 3 rows
*/

DECLARE
    l_entry price_archive_entry;
BEGIN
    FOREACH l_entry IN ARRAY $1
    LOOP

        BEGIN
            PERFORM price_archive_add_entry(l_entry);

            EXCEPTION WHEN OTHERS THEN
                RAISE WARNING 'Exception in price_archive_add_entry(%)', l_entry USING ERRCODE = SQLSTATE, DETAIL = SQLERRM;
                CONTINUE;
        END;
        RETURN NEXT l_entry;

    END LOOP;
END;

$$ LANGUAGE plpgsql;