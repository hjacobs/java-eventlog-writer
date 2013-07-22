CREATE OR REPLACE FUNCTION color_get_colors() RETURNS SETOF color AS
$$
-- $Id$
-- $HeadURL$
/**
 * Get all colors.
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
                 FROM zcat_commons.color;
END;
$$ LANGUAGE 'plpgsql' STABLE SECURITY DEFINER;
