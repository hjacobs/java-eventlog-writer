CREATE OR REPLACE FUNCTION get_app_configs(
    p_only_online boolean,
    OUT result_status_id smallint,
    OUT result_status_msg text,
    OUT app_configs app_config[]
  )
  RETURNS record AS
$BODY$
-- $Id$
-- $HeadURL$
BEGIN
    BEGIN
        app_configs := ARRAY(
            SELECT ROW(ac_id,
                       ac_appdomain_id,
                       ac_key,
                       ac_value,
                       ac_is_online_updateable)
            FROM zcat_commons.app_config
            WHERE NOT p_only_online OR ac_is_online_updateable = p_only_online
            ORDER BY ac_id
        );

        result_status_id := 0;
        --result_status_msg := zcat_commons.scm(result_status_id);
        result_status_msg := 'SUCCESS';
    EXCEPTION WHEN others THEN
        RAISE WARNING 'Exception in get_app_configs(%)', quote_nullable(p_only_online) USING ERRCODE = SQLSTATE, DETAIL = SQLERRM;
        result_status_id:=999;
        result_status_msg:=SQLSTATE::text|| ' '  || SQLERRM;
    END;
    RETURN;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;


