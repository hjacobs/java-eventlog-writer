CREATE OR REPLACE FUNCTION article_get_season (
  p_season_code  text
) RETURNS setof season AS
$$

  SELECT s_code,
         s_name_message_key,
         s_is_deleted,
         s_is_basics,
         s_sort_key,
         s_active_from,
         s_active_to
    FROM zcat_commons.season
   WHERE s_code = $1
   ORDER BY s_sort_key

$$
LANGUAGE SQL STABLE SECURITY DEFINER
COST 100;