CREATE OR REPLACE FUNCTION test_simple_transformer(
    p_obj example_domain_object_with_simple_transformer
    )
RETURNS example_domain_object_with_simple_transformer AS
$BODY$
begin
    return p_obj;
end;
$BODY$
LANGUAGE plpgsql VOLATILE COST 100;
