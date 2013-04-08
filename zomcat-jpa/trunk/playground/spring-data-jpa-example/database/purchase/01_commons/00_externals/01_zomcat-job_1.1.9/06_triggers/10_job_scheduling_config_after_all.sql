CREATE TRIGGER job_scheduling_config_after_all
AFTER INSERT OR UPDATE OR DELETE ON zz_commons.job_scheduling_config
    FOR EACH ROW EXECUTE PROCEDURE zz_commons.job_scheduling_config_after_all_trigger();
