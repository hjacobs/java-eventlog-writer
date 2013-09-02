CREATE TYPE ean_number_range AS (
    id                      integer,
    name                    text,
    active                  boolean,
    ean_number_range_type   zcat_commons.ean_number_range_type,
    start_ean               text,   -- cast EAN13 internal to text for the typemapper to work
    end_ean                 text,
    increase_by             integer,
    last_ean                text
);
