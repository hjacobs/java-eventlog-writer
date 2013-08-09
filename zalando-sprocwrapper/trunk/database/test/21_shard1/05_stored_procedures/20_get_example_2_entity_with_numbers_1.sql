CREATE OR REPLACE FUNCTION get_example_2_entity_with_numbers_1(r example2_domain_object1)
   RETURNS example2_domain_object1 AS
$BODY$
BEGIN
    RETURN r;
END;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;