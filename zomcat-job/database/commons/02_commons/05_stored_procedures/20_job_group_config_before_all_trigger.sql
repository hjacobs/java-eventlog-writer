CREATE OR REPLACE FUNCTION zz_commons.job_group_config_before_all_trigger() RETURNS trigger AS $$
    BEGIN
        IF TG_OP IN ('INSERT','UPDATE') THEN
          NEW.jgc_last_modified = clock_timestamp();
          RETURN NEW;
        ELSE
          RETURN OLD;
        END IF;
    END;
$$ LANGUAGE plpgsql;
