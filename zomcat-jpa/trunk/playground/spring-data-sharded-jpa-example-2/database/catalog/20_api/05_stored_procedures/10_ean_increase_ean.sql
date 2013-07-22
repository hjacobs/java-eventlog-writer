CREATE OR REPLACE FUNCTION ean_increase_ean(
  p_ean     EAN13,
  p_ean_max EAN13,
  p_inc     integer default 1
)
returns EAN13 as
/*
-- $Id$
-- $Id$
-- $HeadURL$
*/
/**
 * Selects a next value for a given EAN13 - but never returns more than p_ean_max
 *
 * @ExpectedExecutionTime 10 ms
 * @ExpectedExecutionFrequency 10.000 per day
 */
/**  Test
  set search_path=zcat_api_r13_00_07,public;
  select * from ean_increase_ean('0000000000000'::EAN13, '000000000009?'::EAN13);       -- returns "000-000000001-7"
  select * from ean_increase_ean('0000000000000'::EAN13, '000000000009?'::EAN13, 2);    -- returns "000-000000002-4"
  select * from ean_increase_ean('123456789012?'::EAN13, '123456789012?'::EAN13);       -- returns "123-456789013-5"
  select * from ean_increase_ean('0000000000000'::EAN13, '000000000009?'::EAN13, 10);   -- returns "000-000000009-3"
  select * from ean_increase_ean('0000000000000'::EAN13, '000000000009?'::EAN13, 100);  -- returns "000-000000009-3"
*/
$BODY$
    select (
        lpad( (
            select min(m)
                from unnest(
                    ARRAY[
                            ( ( replace( $1::text, '-', '' )::bigint / 10 ) + $3 ),
                          (   replace( $2::text, '-', '' )::bigint / 10 )
                       ] ) m
                  )::text, 12, '0' ) || '?' )::EAN13;
$BODY$
language sql immutable strict;
