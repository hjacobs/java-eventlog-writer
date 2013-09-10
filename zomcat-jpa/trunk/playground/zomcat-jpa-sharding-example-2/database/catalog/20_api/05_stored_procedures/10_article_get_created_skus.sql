CREATE OR REPLACE function article_get_created_skus(
    p_skus text[],

    OUT sku text)
RETURNS SETOF text AS
$BODY$
/*
-- $Id$
-- $HeadURL$
*/
/**
 * Returns all skus from the passed skus that have already been created in database.
 *
 *
 * @ExpectedExecutionTime           50 ms
 * @ExpectedExecutionFrequency      every minute
 */
/**  Test
  set search_path = zcat_api;
  select * from article_get_created_skus('{MPreAIg8V, MPreAIg8JJ}'::text[]);
*/
BEGIN

    RETURN QUERY
    SELECT as_sku
      FROM unnest(p_skus) AS requested_skus
      JOIN zcat_data.article_sku ON as_sku = requested_skus;

END
$BODY$
LANGUAGE plpgsql
    VOLATILE SECURITY DEFINER
    COST 100;
