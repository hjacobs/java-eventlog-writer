CREATE OR REPLACE FUNCTION zz_commons.job_group_config_after_all_trigger() RETURNS trigger AS $$
    BEGIN
        -- If Insert
        IF TG_OP  = 'INSERT' THEN
            INSERT INTO zz_commons.job_group_config_history(jgch_type,
                                                          jgch_user,
                                                          jgch_job_group_config_id,
                                                          jgch_group_name,
                                                          jgch_description,
                                                          jgch_app_instance_keys,
                                                          jgch_active,
                                                          jgch_jgc_created,
                                                          jgch_jgc_last_modified,
                                                          jgch_created)
            VALUES ('I',
                    current_user,
                    NEW.jgc_id,
                    NEW.jgc_group_name,
                    NEW.jgc_description,
                    NEW.jgc_app_instance_keys,
                    NEW.jgc_active,
                    NEW.jgc_created,
                    NEW.jgc_last_modified,
                    now());

        END IF;

        -- If Update
        IF TG_OP  = 'UPDATE' THEN
            INSERT INTO zz_commons.job_group_config_history(jgch_type,
                                                          jgch_user,
                                                          jgch_job_group_config_id,
                                                          jgch_group_name,
                                                          jgch_description,
                                                          jgch_app_instance_keys,
                                                          jgch_active,
                                                          jgch_jgc_created,
                                                          jgch_jgc_last_modified,
                                                          jgch_created)
            VALUES ('U',
                    current_user,
                    OLD.jgc_id,
                    OLD.jgc_group_name,
                    OLD.jgc_description,
                    OLD.jgc_app_instance_keys,
                    OLD.jgc_active,
                    OLD.jgc_created,
                    OLD.jgc_last_modified,
                    now());
        END IF;

        -- If Delete
        IF TG_OP  = 'DELETE' THEN
            INSERT INTO zz_commons.job_group_config_history(jgch_type,
                                                          jgch_user,
                                                          jgch_job_group_config_id,
                                                          jgch_group_name,
                                                          jgch_description,
                                                          jgch_app_instance_keys,
                                                          jgch_active,
                                                          jgch_jgc_created,
                                                          jgch_jgc_last_modified,
                                                          jgch_created)
            VALUES ('D',
                    current_user,
                    OLD.jgc_id,
                    OLD.jgc_group_name,
                    OLD.jgc_description,
                    OLD.jgc_app_instance_keys,
                    OLD.jgc_active,
                    OLD.jgc_created,
                    OLD.jgc_last_modified,
                    now());
        END IF;

        RETURN NULL;
    END;
$$ LANGUAGE plpgsql;
