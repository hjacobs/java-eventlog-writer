/*
 * This table holds the Stock Keeping Unit (sku) which uniquely determines
 * an article on a certain abstraction level. the abstraction level is defined
 * by the sku type , which can be:
 *   MODEL:  the most abstract level of an article
 *   CONFIG: a variant of the article discriminated by color / material etc
 *   SIMPLE: a concrete article mainly distinguished by its size
 *
 * The skus of an article model and its more concrete subtypes are arranged in a sku hierarchy:
 *   MODEL
 *      CONFIG1
 *          SIMPLE1
 *          SIMPLE2
 *          ...
 *      CONFIG2
 *      ...
 * The hierarchy is explicitly given by the as_config_id and as_model_id columns that point to the
 * parent(s) sku of the column as_sku, so every SIMPLE sku points to its CONFIG and MODEL sku, each CONFIG
 * points to its MODEL sku.
 *
 * Implicitly the hierarchy is also given inside the sku iself. a sku always starts with its direct parent sku:
 *
 *   MODEL             TI116X009
 *       CONFIG1       TI116X009-802
 *           SIMPLE1   TI116X009-8020360000
 *           SIMPLE2   TI116X009-8020370000
 *           ...
 *       CONFIG2       TI116X009-702
 *           SIMPLE1   TI116X009-7020360000
 *           ...
 *       ...
 *
 * One special case is an article which has legacy skus.
 * A legacy sku is flagged true in the column as_is_legacy.
 * Legacy skus apply to the rules given above but they are special, because their MODEL and CONFIG sku
 * can be identically:
 *
 * NOTE:
 *    It is important to take care when querying for a sku without giving the sku type because this can result
 *    in 2 result rows. So to avoid random behavior either
 *
 *    give an sku type explicitely:
 *
 *      select as_id into l_id
 *        from zcat_data.article_sku
 *       where as_sku = p_sku
 *         and as_sku_type = 'CONFIG';
 *
 *    or distinct on as_sku and order by as_sku_type
 *
 *      select as_id into l_id
 *        from zcat_data.article_sku
 *       where as_sku = p_sku
 *       order
 *          by as_sku_type
 *       limit 1;
 *
 *
 */


create table zcat_data.article_sku (
    as_id                       integer              PRIMARY KEY,
    as_sku                      text                 NOT NULL,
    as_sku_type                 zcat_data.sku_type   NOT NULL,
    as_model_id                 int,
    as_config_id                int,
    as_is_legacy                boolean              NOT NULL,
    as_created                  timestamptz          DEFAULT now(),
    as_created_by               text                 NOT NULL,
    as_flow_id                  text                 NOT NULL,
    CONSTRAINT article_sku_simple_constraint CHECK (as_sku_type <> 'SIMPLE' OR (as_sku_type = 'SIMPLE' AND as_model_id IS NOT NULL AND as_config_id IS NOT NULL)),
    CONSTRAINT article_sku_config_constraint CHECK (as_sku_type <> 'CONFIG' OR (as_sku_type = 'CONFIG' AND as_model_id IS NOT NULL AND as_config_id IS     NULL)),
    CONSTRAINT article_sku_model_constraint  CHECK (as_sku_type <> 'MODEL'  OR (as_sku_type = 'MODEL'  AND as_model_id IS     NULL AND as_config_id IS     NULL)),
    FOREIGN KEY (as_model_id) REFERENCES zcat_data.article_sku (as_id),
    FOREIGN KEY (as_config_id) REFERENCES zcat_data.article_sku (as_id)
);


COMMENT ON TABLE zcat_data.article_sku IS 'contains stock keeping unit (sku) for an article. a SIMPLE sku has a config_id and a model_id, a CONFIG sku has a model_id';
COMMENT ON COLUMN zcat_data.article_sku.as_sku IS 'the sku string which is a text identifier for the article on its certain abstraction level (MODEL,CONFIG,SIMPLE)';
COMMENT ON COLUMN zcat_data.article_sku.as_sku_type IS 'the abstraction level (MODEL,CONFIG,SIMPLE)';
COMMENT ON COLUMN zcat_data.article_sku.as_model_id IS 'points to the MODEL parent of the sku (NULL for MODEL skus)';
COMMENT ON COLUMN zcat_data.article_sku.as_config_id IS 'points to the CONFIG parent of the sku (NULL for MODEL/CONFIG skus)';
COMMENT ON COLUMN zcat_data.article_sku.as_is_legacy IS 'flags this sku as an legacy sku';

create index on zcat_data.article_sku (as_sku, as_sku_type);
create index on zcat_data.article_sku (as_model_id);
create index on zcat_data.article_sku (as_config_id);
create unique index on zcat_data.article_sku (as_sku text_pattern_ops) where not as_is_legacy or as_sku_type = 'SIMPLE';
create unique index on zcat_data.article_sku (as_sku text_pattern_ops, as_sku_type) where as_is_legacy and as_sku_type != 'SIMPLE';

create sequence zcat_data.article_sku_id_simple_seq increment by -1 minvalue -9223372036854775807 start with -1;
create sequence zcat_data.article_sku_id_config_seq increment by 2 start with 2;
create sequence zcat_data.article_sku_id_model_seq increment by 2;
