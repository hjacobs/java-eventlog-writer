CREATE OR REPLACE FUNCTION get_shard_aware_id(p_id_type_name text, p_virtual_shard_id int)
    RETURNS bigint AS
$BODY$
DECLARE
    l_type_code int;
    l_next_sequence_value int;
BEGIN
    SELECT sait_code
      INTO l_type_code
      FROM zcat_commons.shard_aware_id_type
     WHERE sait_name = p_id_type_name;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'shard aware id type with name: % not found', p_id_type_name;
    END IF;

    l_next_sequence_value := NEXTVAL('zcat_data.shard_id_sequence_' || lower(p_id_type_name));

    return (l_type_code::bit(8) || p_virtual_shard_id::bit(24) || l_next_sequence_value::bit(32))::bit(64)::bigint;
END
$BODY$
language plpgsql
STABLE security definer
cost 100;