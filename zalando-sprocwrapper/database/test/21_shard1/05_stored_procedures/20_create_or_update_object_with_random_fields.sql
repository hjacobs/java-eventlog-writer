CREATE OR REPLACE FUNCTION create_or_update_object_with_random_fields(
    p_obj example_domain_object_with_random_fields
    )
RETURNS text AS
$BODY$
begin
    return p_obj.x || p_obj.y || p_obj.z || (p_obj.inner_object).x || (p_obj.inner_object).y || (p_obj.inner_object).z;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
