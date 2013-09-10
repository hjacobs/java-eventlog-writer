RESET role;

CREATE SCHEMA zcat_commons AUTHORIZATION zalando;

GRANT USAGE ON SCHEMA zcat_commons TO zalando_data_usage;
GRANT USAGE ON SCHEMA zcat_commons TO public;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zcat_commons GRANT EXECUTE ON FUNCTIONS TO public;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zcat_commons GRANT EXECUTE ON FUNCTIONS TO zalando_data_writer;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zcat_commons GRANT USAGE ON SEQUENCES TO zalando_data_writer;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zcat_commons GRANT SELECT ON SEQUENCES TO zalando_data_reader;

ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zcat_commons GRANT SELECT ON TABLES TO zalando_data_reader;
ALTER DEFAULT PRIVILEGES FOR ROLE zalando IN SCHEMA zcat_commons GRANT INSERT, DELETE, UPDATE ON TABLES TO zalando_data_writer;

SET role TO zalando;

-- the following lines do not work, as no objects exist at that point
--GRANT SELECT ON ALL TABLES IN SCHEMA zcat_commons TO zalando_data_reader;
--GRANT SELECT ON ALL SEQUENCES IN SCHEMA zcat_commons TO zalando_data_reader;
--GRANT INSERT,DELETE,UPDATE ON ALL TABLES IN SCHEMA zcat_commons TO zalando_data_writer;
--GRANT USAGE ON ALL SEQUENCES IN SCHEMA zcat_commons TO zalando_data_writer;
