CREATE OR REPLACE FUNCTION search_something_else_on_shards(p_param text)
          RETURNS SETOF integer AS
$BODY$
BEGIN
    RETURN QUERY SELECT 1;
END;
$BODY$
          LANGUAGE plpgsql VOLATILE
          COST 100;
