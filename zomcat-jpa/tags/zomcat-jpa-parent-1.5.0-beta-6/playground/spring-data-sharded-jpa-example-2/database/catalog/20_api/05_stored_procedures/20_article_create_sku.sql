CREATE OR REPLACE FUNCTION article_create_sku(
    p_sku               text,
    p_parent_model_id   integer,
    p_parent_config_id  integer,
    p_sku_type          zcat_data.sku_type,
    p_scope             flow_scope
  )
  RETURNS integer AS
$BODY$
-- $Id$
-- $HeadURL$
/**
 * <Stored Procedure simple doc>
 *
 * @ExpectedExecutionTime <time> {m,s,ms}
 * @ExpectedExecutionFrequency {Daily,Hourly,EveryMinute,EveryOrder,EveryLogin,EveryShipment,EveryStockUpdate,EveryCCTUsage}
 */

/* -- test

 -- Stored Procedure simple test-case
 begin;
 set client_min_messages to debug1;
 select * from article_create_sku(sku, model_sku, config_sku);
 rollback;

 */
DECLARE
    l_article_sku_id integer;

BEGIN
  BEGIN
    if p_sku_type = 'SIMPLE'::zcat_data.sku_type and (p_parent_model_id is not null and p_parent_config_id is not null) then
        select nextval ('zcat_data.article_sku_id_simple_seq')
        into l_article_sku_id;
    elsif p_sku_type = 'CONFIG'::zcat_data.sku_type and (p_parent_model_id is not null and p_parent_config_id is null) then
        select nextval ('zcat_data.article_sku_id_config_seq')
        into l_article_sku_id;
    elsif p_sku_type = 'MODEL'::zcat_data.sku_type and (p_parent_model_id is null and p_parent_config_id is null) then
        select nextval ('zcat_data.article_sku_id_model_seq')
        into l_article_sku_id;
    else
        raise exception 'Request article creation inconsistent: sku = %, parent model = % parent config = % for type %.',
            p_sku, p_parent_model_id, p_parent_config_id, p_sku_type;
    end if;

    insert into zcat_data.article_sku (
        as_id,
        as_model_id,
        as_config_id,
        as_sku_type,
        as_sku,
        as_is_legacy,
        as_created_by,
        as_flow_id
    )
    values (
        l_article_sku_id,
        p_parent_model_id,
        p_parent_config_id,
        p_sku_type,
        p_sku,
        position('-' in p_sku) between 1 and 9,
        p_scope.user_id,
        p_scope.flow_id
    );

  EXCEPTION
  WHEN unique_violation THEN
-- this article sku has already been created. re-use the id:
      SELECT as_id INTO l_article_sku_id
        FROM zcat_data.article_sku
       WHERE p_sku = as_sku;
  END;

  return l_article_sku_id;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
