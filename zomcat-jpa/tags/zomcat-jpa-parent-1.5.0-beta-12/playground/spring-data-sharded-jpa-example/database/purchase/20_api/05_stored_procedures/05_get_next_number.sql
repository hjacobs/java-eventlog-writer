CREATE OR REPLACE FUNCTION get_next_number (
  p_type  zzj_data.number_range_type
) RETURNS text
AS
$$

  SELECT zzj_data.get_next_number($1);

$$
LANGUAGE SQL
VOLATILE SECURITY DEFINER;