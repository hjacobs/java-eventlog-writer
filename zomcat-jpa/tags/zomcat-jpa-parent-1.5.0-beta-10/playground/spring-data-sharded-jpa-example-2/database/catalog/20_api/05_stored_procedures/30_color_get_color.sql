CREATE OR REPLACE FUNCTION color_get_color(
    p_code text
) RETURNS SETOF color AS
$$
-- $Id$
-- $HeadURL$
/**
 * Get color by code.
 *
 * @ExpectedExecutionTime 100 ms
 * @ExpectedExecutionFrequency Daily
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from color_get_all_colors();
 rollback;

 */
BEGIN
  RETURN QUERY SELECT c_code,
                      c_name_message_key,
                      c_family_code
                 FROM zcat_commons.color
                WHERE c_code = p_code;
END;
$$ LANGUAGE 'plpgsql' STABLE SECURITY DEFINER;
