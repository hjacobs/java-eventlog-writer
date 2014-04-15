-- zz_commons is expected to be there

reset role;
SELECT zz_utils.set_project_schema_owner_role('zz_commons');
SET search_path to zz_commons, public;