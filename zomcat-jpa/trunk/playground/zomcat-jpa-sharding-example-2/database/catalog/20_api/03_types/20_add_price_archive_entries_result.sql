CREATE TYPE add_price_archive_entries_result AS (
  inserted_into_price_current   add_price_archive_entries_simple_result[],
  updated_price_current         add_price_archive_entries_simple_result[],
  inserted_into_price_archive   add_price_archive_entries_simple_result[],
  deleted_from_price_current    add_price_archive_entries_simple_result[]
);