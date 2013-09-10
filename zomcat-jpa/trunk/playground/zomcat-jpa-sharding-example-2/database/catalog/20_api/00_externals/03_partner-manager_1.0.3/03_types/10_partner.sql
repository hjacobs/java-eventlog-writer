CREATE TYPE partner AS (
  id                 integer,
  shipping_countries varchar[],
  appdomain_ids      smallint[],
  name               text,
  enabled            boolean,
  stock_id           smallint
);
