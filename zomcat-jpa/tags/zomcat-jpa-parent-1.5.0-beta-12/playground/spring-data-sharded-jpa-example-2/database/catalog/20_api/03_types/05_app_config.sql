CREATE TYPE app_config AS (
  id integer,
  appdomain_id smallint,
  config_key varchar(255),
  config_value text,
  online_updateable boolean
);
