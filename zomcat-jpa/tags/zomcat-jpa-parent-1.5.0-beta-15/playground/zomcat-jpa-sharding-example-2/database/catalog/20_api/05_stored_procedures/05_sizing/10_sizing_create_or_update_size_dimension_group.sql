CREATE OR REPLACE FUNCTION sizing_create_or_update_size_dimension_group(
  p_group size_dimension_group,
  p_scope flow_scope
)
  RETURNS int AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test

  set search_path=zcat_api_r12_00_40;
  SELECT * FROM create_model_sku ('10K11A008');
*/
DECLARE
  l_group_id int;
  l_dimension_codes text[];
BEGIN

    RAISE INFO 'sizing_create_or_update_dimension_group(%,%,%)', p_group, p_scope.user_id, p_scope.flow_id;
    IF EXISTS(
        SELECT null
          FROM unnest(p_group.bindings)
          LEFT
          JOIN zcat_commons.size_dimension
            ON sd_code = dimension_code
         WHERE sd_code IS NULL
    ) THEN
        RAISE EXCEPTION 'dimension code does not exist';
    END IF;

    IF EXISTS(
        SELECT null
          FROM unnest(p_group.bindings)
          LEFT
          JOIN zcat_commons.size_dimension_prefix
            ON sdp_id = prefix_id
         WHERE prefix_id IS NOT NULL
           AND sdp_id IS NULL
    ) THEN
        RAISE EXCEPTION 'seperator id does not exist';
    END IF;

    l_dimension_codes := ARRAY(select dimension_code from unnest(p_group.bindings));
    l_group_id := sizing_get_dimension_group_id(l_dimension_codes);


    IF l_group_id IS NULL THEN
        SELECT nextval('zcat_commons.size_dimension_group_sdg_id_seq')
          INTO l_group_id;

        INSERT
          INTO zcat_commons.size_dimension_group (
                 sdg_id,
                 sdg_created_by,
                 sdg_last_modified_by,
                 sdg_flow_id
               )
        VALUES (
                 l_group_id,
                 p_scope.user_id,
                 p_scope.user_id,
                 p_scope.flow_id
               );

        INSERT
          INTO zcat_commons.size_dimension_group_binding (
                 sdgb_code,
                 sdgb_group_id,
                 sdgb_position,
                 sdgb_prefix_id,
                 sdgb_created_by,
                 sdgb_last_modified_by,
                 sdgb_flow_id
               )
                select dimension_code,
                       l_group_id,
                       position,
                       prefix_id,
                       p_scope.user_id,
                       p_scope.user_id,
                       p_scope.flow_id
                  from unnest(p_group.bindings);
    ELSE
       -- update
       UPDATE zcat_commons.size_dimension_group_binding
          SET sdgb_prefix_id        = prefix_id,
              sdgb_position         = position,
              sdgb_last_modified    = now(),
              sdgb_last_modified_by = p_scope.user_id,
              sdgb_flow_id          = p_scope.flow_id
         FROM unnest(p_group.bindings)
        WHERE sdgb_code = dimension_code
          AND sdgb_group_id = l_group_id;

    END IF;
    RETURN l_group_id;
END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
