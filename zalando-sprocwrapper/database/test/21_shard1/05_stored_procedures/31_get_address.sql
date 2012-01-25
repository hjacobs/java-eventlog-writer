CREATE FUNCTION get_address(a address_type) RETURNS address_type AS
$$
DECLARE
  b address_type;
BEGIN
  select (a_id,a_customer_id,a_street,a_number)::address_type into b from address where a_id = a.id;
  return b;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;
