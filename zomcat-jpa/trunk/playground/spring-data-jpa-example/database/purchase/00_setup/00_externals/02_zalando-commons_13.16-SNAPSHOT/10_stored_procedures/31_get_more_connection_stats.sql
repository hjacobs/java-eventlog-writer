-- DROP FUNCTION public.get_more_connection_stats();
DO $SQL$
BEGIN

IF string_to_array( split_part( version(), ' ', 2), '.' )::int[] < ARRAY[9,2] THEN

    CREATE OR REPLACE FUNCTION public.get_more_connection_stats(OUT usename text, OUT connections bigint, OUT idle_connections bigint, OUT idle_in_trans_1min bigint, OUT idle_in_trans_5min bigint, OUT max_seconds_idle_in_trans bigint) RETURNS setof record AS
    $$
    SELECT usename::text,
           count(1) AS connections,
           count(CASE WHEN current_query ='<IDLE>' THEN 1 ELSE NULL END) AS idle_connections,
           count(CASE WHEN current_query = '<IDLE> in transaction' AND ((now()-xact_start) > '1min'::interval) THEN 1 ELSE NULL END) AS idle_in_trans_1min,
           count(CASE WHEN current_query LIKE '<IDLE> in transaction' AND ((now()-xact_start) > '5min'::interval) THEN 1 ELSE NULL END) AS idle_in_trans_5min,
           max(CASE WHEN current_query LIKE '<IDLE> in transaction' THEN EXTRACT( 'epoch' from now()-xact_start )::bigint ELSE NULL END) AS max_seconds_idle_in_trans
      FROM pg_stat_activity
     GROUP BY usename;
    $$ LANGUAGE SQL SECURITY DEFINER;

ELSE

    -- new code for version >= 9.2
    CREATE OR REPLACE FUNCTION public.get_more_connection_stats(OUT usename text, OUT connections bigint, OUT idle_connections bigint, OUT idle_in_trans_1min bigint, OUT idle_in_trans_5min bigint, OUT max_seconds_idle_in_trans bigint) RETURNS setof record AS
    $$
    SELECT usename::text,
           count(1) AS connections,
           count(CASE WHEN state='idle' THEN 1 ELSE NULL END) AS idle_connections,
           count(CASE WHEN state='idle in transaction' AND ((now()-xact_start) > '1min'::interval) THEN 1 ELSE NULL END) AS idle_in_trans_1min,
           count(CASE WHEN state='idle in transaction' AND ((now()-xact_start) > '5min'::interval) THEN 1 ELSE NULL END) AS idle_in_trans_5min,
           max(CASE WHEN state='idle in transaction' THEN EXTRACT( 'epoch' from now()-xact_start )::bigint ELSE NULL END) AS max_seconds_idle_in_trans
      FROM pg_stat_activity
     GROUP BY usename;
    $$ LANGUAGE SQL SECURITY DEFINER;

END IF;

END;
$SQL$;

ALTER FUNCTION public.get_more_connection_stats() OWNER TO postgres;
GRANT EXECUTE ON FUNCTION public.get_more_connection_stats() TO public;
