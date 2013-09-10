create or replace function price_conf_mgmt_create_price_level (
    p_price_level price_level
) returns void as
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
declare
    l_id int;
begin

    insert into zcat_commons.price_level
           (pl_id, pl_level, pl_name)
    values (p_price_level.id, p_price_level.level, p_price_level.name);

end
$BODY$

language plpgsql
  volatile
  security definer
  cost 100;
