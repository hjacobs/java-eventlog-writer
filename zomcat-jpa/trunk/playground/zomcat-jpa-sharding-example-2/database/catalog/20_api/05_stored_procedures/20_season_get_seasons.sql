CREATE OR REPLACE FUNCTION season_get_seasons() RETURNS SETOF season AS
$$
BEGIN
  RETURN QUERY SELECT s_code,
                      s_name_message_key,
                      s_is_deleted,
                      s_is_basics,
                      s_sort_key,
                      s_active_from,
                      s_active_to
                 FROM zcat_commons.season
             ORDER BY s_sort_key;
END;
$$ LANGUAGE 'plpgsql' STABLE SECURITY DEFINER;
