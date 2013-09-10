create or replace function price_conf_mgmt_get_all_price_levels (
    OUT id smallint,
    OUT level smallint,
    OUT name text,
    OUT promotional boolean
) returns setof record as
$BODY$
/*
-- $Id:  $
-- $HeadURL:  $
*/
begin
    return query
    select
        pl_id as id,
        pl_level as level,
        pl_name as name,
        pl_is_promotional as promotional
    from zcat_commons.price_level;
end
$BODY$

language plpgsql
  volatile
  security definer
  cost 100;
