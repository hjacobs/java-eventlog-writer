RESET role;

CREATE SCHEMA ztest_shard
 AUTHORIZATION zalando_api_owner;

ALTER DEFAULT PRIVILEGES FOR ROLE postgres IN SCHEMA ztest_shard REVOKE EXECUTE ON FUNCTIONS FROM public;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando_api_owner IN SCHEMA ztest_shard REVOKE EXECUTE ON FUNCTIONS FROM public;
ALTER DEFAULT PRIVILEGES IN SCHEMA ztest_shard REVOKE EXECUTE ON FUNCTIONS FROM public;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando_api_owner IN SCHEMA ztest_shard GRANT EXECUTE ON FUNCTIONS TO zalando_api_executor;

GRANT USAGE ON SCHEMA ztest_shard TO zalando_api_usage;

SET ROLE TO zalando_api_owner;

SET search_path TO ztest_shard, public;
