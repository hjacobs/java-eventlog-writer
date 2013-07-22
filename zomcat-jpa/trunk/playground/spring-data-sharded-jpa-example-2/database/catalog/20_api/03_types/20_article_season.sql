CREATE TYPE article_season AS (
  code              text,
  created_by        text,
  last_modified_by  text,
  flow_id           text,
  name_message_key  text,
  is_deleted        boolean,
  sort_key          integer
);
