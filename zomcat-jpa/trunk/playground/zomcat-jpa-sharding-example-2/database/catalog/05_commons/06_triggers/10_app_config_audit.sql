CREATE TRIGGER app_config_audit
AFTER INSERT OR UPDATE OR DELETE ON zcat_commons.app_config
    FOR EACH ROW EXECUTE PROCEDURE zcat_commons.app_conf_audit_trigger();
