CREATE TRIGGER job_scheduling_config_before_all
BEFORE INSERT OR UPDATE OR DELETE ON zz_commons.job_group_config
    FOR EACH ROW EXECUTE PROCEDURE zz_commons.job_group_config_before_all_trigger();
