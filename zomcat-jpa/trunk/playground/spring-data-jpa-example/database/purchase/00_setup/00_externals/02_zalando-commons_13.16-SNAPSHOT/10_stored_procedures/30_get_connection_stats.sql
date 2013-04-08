-- DROP FUNCTION public.get_connection_stats();
CREATE OR REPLACE FUNCTION public.get_connection_stats(OUT usename text, OUT connections bigint, OUT idle_connections bigint) RETURNS setof record AS
$$
select usename::text, count(1) AS connections, count(CASE WHEN current_query='<IDLE>' THEN 1 ELSE NULL END) AS idle_connections FROM pg_stat_activity GROUP BY usename;
$$ LANGUAGE SQL SECURITY DEFINER;
ALTER FUNCTION public.get_connection_stats() OWNER TO postgres;
GRANT EXECUTE ON FUNCTION public.get_connection_stats() TO public;
