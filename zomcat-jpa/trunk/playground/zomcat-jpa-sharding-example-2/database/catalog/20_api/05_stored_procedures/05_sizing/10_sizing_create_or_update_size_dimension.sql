CREATE OR REPLACE FUNCTION sizing_create_or_update_size_dimension(
  p_dimension   size_dimension,
  p_scope       flow_scope
)
  RETURNS void AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test

  set search_path=zcat_api_r12_00_40;
  SELECT * FROM create_model_sku ('10K11A008');
*/
DECLARE
BEGIN
    RAISE INFO 'SIZE_CHART %', p_dimension;
    IF length(p_dimension.code) != 2 THEN
        RAISE EXCEPTION 'size dimension must have a 2-digit code. given code is "%"', size_dimension.code;
    END IF;

    UPDATE zcat_commons.size_dimension
       SET sd_name              = p_dimension.name,
           sd_last_modified     = now(),
           sd_last_modified_by  = p_scope.user_id,
           sd_flow_id           = p_scope.flow_id
     WHERE sd_code = p_dimension.code;

     IF NOT FOUND THEN
       INSERT
         INTO zcat_commons.size_dimension (
                sd_code,
                sd_name,
                sd_display_message_key,
                sd_created_by,
                sd_last_modified_by,
                sd_flow_id
              )
       VALUES (
                p_dimension.code,
                p_dimension.name,
                p_dimension.display_message_key,
                p_scope.user_id,
                p_scope.user_id,
                p_scope.flow_id
              );
     END IF;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
