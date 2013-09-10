CREATE TABLE zcat_commons.app_config
(
  ac_id serial,
  ac_appdomain_id smallint,
  ac_key varchar(255) NOT NULL,
  ac_value text,
  ac_is_online_updateable boolean NOT NULL,
  ac_created timestamp without time zone NOT NULL DEFAULT now(),
  ac_last_modified timestamp without time zone NOT NULL DEFAULT clock_timestamp(),
  PRIMARY KEY (ac_id),
  UNIQUE (ac_key, ac_appdomain_id)
);

COMMENT ON TABLE zcat_commons.app_config IS 'Hier werden verschiedene Konfigurationseinträge definiert. Ein Eintrag kann appDomain Abhängig sein.';
COMMENT ON COLUMN zcat_commons.app_config.ac_is_online_updateable IS 'Dient der Unterscheidung zwischen festen und zur Laufzeit in die Applikation aktualisierbaren Konfigurationswerten.';

-- ALTER TABLE zcat_commons.app_config OWNER TO zalando;

CREATE UNIQUE INDEX app_config_global_ac_key_idx ON zcat_commons.app_config ( ac_key ) WHERE ac_appdomain_id IS NULL;

GRANT INSERT, DELETE , UPDATE ON zcat_commons.app_config TO zalando_24x7;
