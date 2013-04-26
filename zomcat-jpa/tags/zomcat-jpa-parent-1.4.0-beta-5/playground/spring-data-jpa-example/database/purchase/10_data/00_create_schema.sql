RESET ROLE;

CREATE SCHEMA zzj_data
  AUTHORIZATION zalando;

GRANT USAGE ON SCHEMA zzj_data TO zalando_data_usage;
GRANT USAGE ON SCHEMA zzj_data TO zalando_purchase_usage;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data REVOKE EXECUTE ON FUNCTIONS FROM PUBLIC;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT EXECUTE ON FUNCTIONS TO zalando_data_writer;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT USAGE ON SEQUENCES TO zalando_data_writer;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT SELECT ON SEQUENCES TO zalando_data_reader;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT SELECT ON TABLES TO zalando_data_reader;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT INSERT, DELETE, UPDATE ON TABLES TO zalando_data_writer;

-- create purchase specific roles
ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT SELECT ON SEQUENCES TO zalando_purchase_reader;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT USAGE ON SEQUENCES TO zalando_purchase_writer;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT EXECUTE ON FUNCTIONS TO zalando_purchase_writer;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT SELECT ON TABLES TO zalando_purchase_reader;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zzj_data GRANT INSERT, DELETE, UPDATE ON TABLES TO zalando_purchase_writer;

SET role TO zalando;
