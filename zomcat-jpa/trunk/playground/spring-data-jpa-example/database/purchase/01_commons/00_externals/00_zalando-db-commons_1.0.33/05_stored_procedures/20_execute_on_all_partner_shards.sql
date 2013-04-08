-- DROP FUNCTION zz_commons.execute_on_all_partners(text, text);

CREATE OR REPLACE FUNCTION zz_commons.execute_on_all_partners(p_sql text, p_statement_timeout text default null, OUT result hstore)
RETURNS SETOF hstore AS
$BODY$
-- $Id$
-- $HeadURL$
/**
 * This stored procedure will execute a read-only SELECT query on all configured and enabled partner databases.
 * It returns the results in the form of an HSTORE value per returned ROW.
 */
/* -- test
  set client_min_messages to 'DEBUG1';
  set role to nagios;
  select result -> 'bla', result -> 'blu' from zz_commons.execute_on_all_partners('select 1 as bla, ''kuku'' as blu union select 2, ''koko'';');
  reset role;

  select result from zz_commons.execute_on_all_partners('select 1 as bla, pg_sleep(5);', '6s');
  select result from zz_commons.execute_on_all_partners('select 1 as bla, pg_sleep(5);');

  select sum( (result -> 'count')::bigint ) from zz_commons.execute_on_all_partners($$SELECT count(1) FROM zpa_queue.sales_order_header where soh_status_id=0 AND soh_created < now() - '2 hours'::interval$$);

  select * from zz_commons.partner;
  select dblink_disconnect('dblink_partner1_queue');
  select dblink_disconnect('dblink_partner2_queue');

 */
DECLARE
  l_conn_array text[] := NULL;
  l_conn_count int;
  l_conn text;
  l_failed_query_count int;
  l_failed_conn_error_array text[];
  l_send_query_result int;
  l_remote_text text;
  t timestamp;
BEGIN
  -- open connections
  t := clock_timestamp();
  select array_agg(p_remote_server_name || '_' || pg_backend_pid()::text ),
         count(dblink_connect(p_remote_server_name || '_' || pg_backend_pid()::text, p_remote_server_name ))
    into l_conn_array,
         l_conn_count
    from ( select distinct p_remote_server_name from zz_commons.partner ) as r;
  if l_conn_count = 0 then
    raise exception 'No enabled partner configurations found';
  end if;
  raise debug 'opened remote connections [%] in %', array_to_string(l_conn_array, ', '), clock_timestamp() - t;
  -- set connections to be read only
  t := clock_timestamp();
  perform dblink(conn, 'SET TRANSACTION READ ONLY;' ) from unnest(l_conn_array) as u(conn);
  raise debug 'transactions set to READ ONLY for remote connections [%] in %', array_to_string(l_conn_array, ', '), clock_timestamp() - t;

  -- set statement_timeout if needed
  if p_statement_timeout is not null then
    t := clock_timestamp();
    perform dblink(conn, 'SET statement_timeout to ' || quote_literal(p_statement_timeout) ) from unnest(l_conn_array) as u(conn);
    raise debug 'statement timeout set to % for remote connections [%] in %', p_statement_timeout, array_to_string(l_conn_array, ', '), clock_timestamp() - t;
  end if;
  -- send queries
  t := clock_timestamp();
  select array_agg(conn || ': [' || dblink_error_message(conn) || ']'), -- get failed query info
         count(conn)::integer
    into l_failed_conn_error_array,
         l_failed_query_count
    from (select conn,
                 dblink_send_query(conn, $$select hstore(___r.*) from ($$ || rtrim(p_sql, E'\n\r\t\f; ') || $$) as ___r;$$) as result -- open connection
            from unnest(l_conn_array) as u(conn)
          offset 0
          ) as q
   where q.result = 0;
  if l_failed_query_count > 0 then
    raise exception 'Could not send queries to partner shards with connections %', array_to_string(l_failed_conn_error_array,',');
  end if;
  raise debug 'sent query to remote databases in %', clock_timestamp() - t;
  -- collect data
  t := clock_timestamp();
  for l_conn in select conn from unnest(l_conn_array) as u(conn)
  loop
    raise debug 'fetching results from [%]', l_conn;
    return query select remote_text from dblink_get_result(l_conn, true) as r(remote_text hstore);
  end loop;
  raise debug 'fetched query results in %', clock_timestamp() - t;

  -- depleating data
  t := clock_timestamp();
  for l_conn in select conn from unnest(l_conn_array) as u(conn)
  loop
    raise debug 'depleating results from [%]', l_conn;
    return query select remote_text from dblink_get_result(l_conn, true) as r(remote_text hstore);
  end loop;
  raise debug 'depleated query results in %', clock_timestamp() - t;

  -- close connections
  t := clock_timestamp();
  perform dblink_disconnect(conn) from unnest(l_conn_array) as u(conn);
  raise debug 'closed connections [%] in %', array_to_string(l_conn_array, ', '), clock_timestamp() - t;
  return;
EXCEPTION
WHEN OTHERS OR query_canceled THEN
  if l_conn_count > 0 then
    raise debug 'closing connections [%] in exception block', array_to_string(l_conn_array, ', ');
    perform dblink_disconnect(conn) from unnest(l_conn_array) as u(conn);
  end if;
  RAISE;
END;
$BODY$
LANGUAGE plpgsql;
ALTER FUNCTION zz_commons.execute_on_all_partners(text, text) OWNER TO zalando;
GRANT EXECUTE ON FUNCTION zz_commons.execute_on_all_partners(text, text) TO public;
