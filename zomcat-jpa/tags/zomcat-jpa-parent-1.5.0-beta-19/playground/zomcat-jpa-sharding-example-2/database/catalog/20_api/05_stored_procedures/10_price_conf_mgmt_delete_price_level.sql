create or replace function price_conf_mgmt_delete_price_level (
    p_price_level price_level
) returns void as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
BEGIN

    delete from zcat_commons.price_level where pl_id = (p_price_level).id;

END
$BODY$
language plpgsql
  volatile
  security definer
  cost 100;

