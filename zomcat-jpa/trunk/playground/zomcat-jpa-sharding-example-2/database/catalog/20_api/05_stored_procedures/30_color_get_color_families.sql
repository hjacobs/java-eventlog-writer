CREATE OR REPLACE FUNCTION color_get_color_families() RETURNS SETOF color_family AS
$$
-- $Id$
-- $HeadURL$
/**
 * Get all color families.
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
                 FROM zcat_commons.color_family;
END;
$$ LANGUAGE 'plpgsql' STABLE SECURITY DEFINER;
