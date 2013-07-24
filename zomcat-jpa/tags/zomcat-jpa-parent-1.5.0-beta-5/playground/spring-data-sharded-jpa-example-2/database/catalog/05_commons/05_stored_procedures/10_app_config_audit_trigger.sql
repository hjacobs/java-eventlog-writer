CREATE OR REPLACE FUNCTION zcat_commons.app_conf_audit_trigger() RETURNS trigger AS $$
    BEGIN

        IF TG_OP = 'INSERT' THEN
          INSERT INTO zcat_commons.app_config_change_log ( accl_changed_on , accl_user , accl_op , accl_appdomain_id_old, accl_appdomain_id_new, accl_key_old, accl_key_new , accl_value_old, accl_value_new , accl_is_online_updateable_old , accl_is_online_updateable_new )
               VALUES ( current_timestamp , session_user , 'I' , NULL , NEW.ac_appdomain_id, NULL , NEW.ac_key , NULL , NEW.ac_value , NULL , NEW.ac_is_online_updateable );
        ELSIF TG_OP = 'UPDATE' THEN
          INSERT INTO zcat_commons.app_config_change_log ( accl_changed_on , accl_user , accl_op , accl_appdomain_id_old, accl_appdomain_id_new, accl_key_old, accl_key_new , accl_value_old, accl_value_new , accl_is_online_updateable_old , accl_is_online_updateable_new  )
               VALUES ( current_timestamp , session_user , 'U' , OLD.ac_appdomain_id , NEW.ac_appdomain_id, OLD.ac_key , NEW.ac_key , OLD.ac_value, NEW.ac_value , OLD.ac_is_online_updateable , NEW.ac_is_online_updateable  );
        ELSIF TG_OP = 'DELETE' THEN
          INSERT INTO zcat_commons.app_config_change_log ( accl_changed_on , accl_user , accl_op , accl_appdomain_id_old, accl_appdomain_id_new, accl_key_old, accl_key_new , accl_value_old, accl_value_new , accl_is_online_updateable_old , accl_is_online_updateable_new  )
               VALUES ( current_timestamp , session_user , 'D',  OLD.ac_appdomain_id , NULL , OLD.ac_key , NULL , OLD.ac_value, NULL , OLD.ac_is_online_updateable , NULL );
        END IF;

        RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
