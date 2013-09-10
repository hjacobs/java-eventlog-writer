CREATE OR REPLACE FUNCTION season_create_season(
  p_in season,
  p_created_by text,
  p_flow_id text
) RETURNS void AS
$$
BEGIN
  INSERT INTO zcat_commons.season (
    s_code,
    s_created_by,
    s_last_modified_by,
    s_flow_id,
    s_name_message_key,
    s_is_deleted,
    s_is_basics,
    s_sort_key,
    s_active_from,
    s_active_to
  )
  SELECT
    p_in.season_code,
    p_created_by,
    p_created_by,
    p_flow_id,
    p_in.name_message_key,
    p_in.is_deleted,
    p_in.is_basics,
    p_in.sort_key,
    p_in.active_from,
    p_in.active_to;
END;
$$ LANGUAGE 'plpgsql' SECURITY DEFINER;
