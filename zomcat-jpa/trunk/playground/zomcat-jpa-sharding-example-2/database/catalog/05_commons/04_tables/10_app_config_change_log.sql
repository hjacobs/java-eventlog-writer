CREATE TABLE zcat_commons.app_config_change_log (
 accl_id serial,
 accl_changed_on timestamp with time zone,
 accl_user character varying(100),
 accl_op "char",
 accl_appdomain_id_old integer,
 accl_appdomain_id_new integer,
 accl_key_old  character varying(255),
 accl_key_new  character varying(255),
 accl_value_old text,
 accl_value_new text,
 accl_is_online_updateable_old boolean,
 accl_is_online_updateable_new boolean,
 PRIMARY KEY ( accl_id )
);

CREATE INDEX app_config_change_log_changed_on_idx ON zcat_commons.app_config_change_log USING btree(accl_changed_on DESC);
