CREATE OR REPLACE FUNCTION zzj_data.get_next_number (
  p_type  zzj_data.number_range_type
  ) RETURNS text AS
$BODY$
-- $Id$
-- $HeadURL$
DECLARE
    l_number  text;
    l_prefix  text;
    l_length  smallint;
BEGIN

    SELECT to_char(nextval('zzj_data.' || nr_seq_name), 'FM9999999999'),
           COALESCE(nr_prefix, ''),
           nr_length
      INTO l_number,
           l_prefix,
           l_length
      FROM zzj_data.number_range
     WHERE nr_type = p_type;

    IF l_number IS NULL THEN
        RAISE EXCEPTION 'Configuration not found for number range type %', p_type;
    END IF;

    IF l_length IS NOT NULL AND length(l_number) > l_length THEN
        RAISE EXCEPTION 'Number of type % out of bounds. Maximum number of digits: %', p_type, l_length;

    END IF;

    RETURN  l_prefix || lpad(l_number, l_length, '0');

END;
$BODY$
LANGUAGE 'plpgsql'
VOLATILE SECURITY DEFINER;