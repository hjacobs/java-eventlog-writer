CREATE OR REPLACE FUNCTION color_get_color_family(
    p_code text
) RETURNS SETOF color_family AS
$$
-- $Id$
-- $HeadURL$
/**
 * Get color family by code.
 *
 * @ExpectedExecutionTime 100 ms
 * @ExpectedExecutionFrequency Daily
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from color_get_all_color_families();
 rollback;

 */
BEGIN
  RETURN QUERY SELECT cf_code,
                      cf_name_message_key
                 FROM zcat_commons.color_family
                WHERE cf_code = p_code;
END;
$$ LANGUAGE 'plpgsql' STABLE SECURITY DEFINER;
