RESET role;

CREATE SCHEMA zcat_api AUTHORIZATION zalando_api_owner;

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA zcat_api REVOKE EXECUTE ON FUNCTIONS FROM public;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando_api_owner IN SCHEMA zcat_api REVOKE EXECUTE ON FUNCTIONS FROM public;
ALTER DEFAULT PRIVILEGES IN SCHEMA zcat_api REVOKE EXECUTE ON FUNCTIONS FROM public;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando_api_owner IN SCHEMA zcat_api GRANT EXECUTE ON FUNCTIONS TO zalando_api_executor;

GRANT USAGE ON SCHEMA zcat_api TO zalando_api_usage;

DO $SQL$
BEGIN
  IF CURRENT_DATABASE() ~ '^(be_staging|fe_staging|prod|testing|integration|development)_bm_db$' THEN
    EXECUTE 'ALTER DATABASE ' || CURRENT_DATABASE() || ' SET search_path to zcat_api, public;';
  END IF;
END
$SQL$;

SET ROLE TO zalando_api_owner;

SET search_path to zcat_api, public;
