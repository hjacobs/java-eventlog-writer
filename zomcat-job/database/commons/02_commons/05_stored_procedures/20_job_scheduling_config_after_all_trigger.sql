CREATE OR REPLACE FUNCTION zz_commons.job_scheduling_config_after_all_trigger() RETURNS trigger AS $$
    BEGIN
        -- If Insert
        IF TG_OP  = 'INSERT' THEN
            INSERT INTO zz_commons.job_scheduling_config_history(jsch_type,
                                                               jsch_user,
                                                               jsch_job_scheduling_config_id,
                                                               jsch_job_class,
                                                               jsch_cron_expression,
                                                               jsch_description,
                                                               jsch_active,
                                                               jsch_processing_limit,
                                                               jsch_startup_processing_limit,
                                                               jsch_app_instance_keys,
                                                               jsch_job_data,
                                                               jsch_job_group_config_id,
                                                               jsch_jsc_created,
                                                               jsch_jsc_last_modified,
                                                               jsch_created)
            VALUES ('I',
                    current_user,
                    NEW.jsc_id,
                    NEW.jsc_job_class,
                    NEW.jsc_cron_expression,
                    NEW.jsc_description,
                    NEW.jsc_active,
                    NEW.jsc_processing_limit,
                    NEW.jsc_startup_processing_limit,
                    NEW.jsc_app_instance_keys,
                    NEW.jsc_job_data,
                    NEW.jsc_job_group_config_id,
                    NEW.jsc_created,
                    NEW.jsc_last_modified,
                    now());

        END IF;

        -- If Update
        IF TG_OP  = 'UPDATE' THEN
            INSERT INTO zz_commons.job_scheduling_config_history(jsch_type,
                                                               jsch_user,
                                                               jsch_job_scheduling_config_id,
                                                               jsch_job_class,
                                                               jsch_cron_expression,
                                                               jsch_description,
                                                               jsch_active,
                                                               jsch_processing_limit,
                                                               jsch_startup_processing_limit,
                                                               jsch_app_instance_keys,
                                                               jsch_job_data,
                                                               jsch_job_group_config_id,
                                                               jsch_jsc_created,
                                                               jsch_jsc_last_modified,
                                                               jsch_created)
            VALUES ('U',
                    current_user,
                    OLD.jsc_id,
                    OLD.jsc_job_class,
                    OLD.jsc_cron_expression,
                    OLD.jsc_description,
                    OLD.jsc_active,
                    OLD.jsc_processing_limit,
                    OLD.jsc_startup_processing_limit,
                    OLD.jsc_app_instance_keys,
                    OLD.jsc_job_data,
                    OLD.jsc_job_group_config_id,
                    OLD.jsc_created,
                    OLD.jsc_last_modified,
                    now());
        END IF;

        -- If Delete
        IF TG_OP  = 'DELETE' THEN
            INSERT INTO zz_commons.job_scheduling_config_history(jsch_type,
                                                               jsch_user,
                                                               jsch_job_scheduling_config_id,
                                                               jsch_job_class,
                                                               jsch_cron_expression,
                                                               jsch_description,
                                                               jsch_active,
                                                               jsch_processing_limit,
                                                               jsch_startup_processing_limit,
                                                               jsch_app_instance_keys,
                                                               jsch_job_data,
                                                               jsch_job_group_config_id,
                                                               jsch_jsc_created,
                                                               jsch_jsc_last_modified,
                                                               jsch_created)
            VALUES ('D',
                    current_user,
                    OLD.jsc_id,
                    OLD.jsc_job_class,
                    OLD.jsc_cron_expression,
                    OLD.jsc_description,
                    OLD.jsc_active,
                    OLD.jsc_processing_limit,
                    OLD.jsc_startup_processing_limit,
                    OLD.jsc_app_instance_keys,
                    OLD.jsc_job_data,
                    OLD.jsc_job_group_config_id,
                    OLD.jsc_created,
                    OLD.jsc_last_modified,
                    now());
        END IF;

        RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
