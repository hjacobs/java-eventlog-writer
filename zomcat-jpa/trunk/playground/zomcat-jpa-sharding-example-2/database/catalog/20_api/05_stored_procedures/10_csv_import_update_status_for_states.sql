CREATE OR REPLACE FUNCTION csv_import_update_status_for_states(
  p_csv_import_state_uuids text[],
  p_csv_import_status zcat_commons.csv_import_status
)
RETURNS void AS
$BODY$
BEGIN
    UPDATE zcat_commons.csv_import_state
       SET cis_last_modified  = now(),
           cis_status         = p_csv_import_status
     WHERE cis_uuid = ANY(p_csv_import_state_uuids);
END
$BODY$
language plpgsql
    volatile security definer
    cost 100;