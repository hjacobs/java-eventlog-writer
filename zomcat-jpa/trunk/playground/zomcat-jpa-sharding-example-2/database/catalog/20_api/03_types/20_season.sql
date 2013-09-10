CREATE TYPE season AS (
  season_code       text,
  name_message_key  text,
  is_deleted        boolean,
  is_basics         boolean,
  sort_key          integer,
  active_from       timestamptz,
  active_to         timestamptz
);
