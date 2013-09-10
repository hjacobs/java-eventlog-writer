create table zcat_data.article_simple_ean (
    ase_id                serial      PRIMARY KEY,
    ase_ean               EAN13       NOT NULL,
    ase_valid_from        timestamptz NOT NULL DEFAULT date '2000-01-01+0',
    ase_is_active         boolean     NOT NULL DEFAULT TRUE,
    ase_simple_sku_id     int         NOT NULL references zcat_data.article_sku (as_id),
    ase_created           timestamptz NOT NULL DEFAULT clock_timestamp(),
    ase_created_by        text,
    ase_flow_id           text
);

create unique index on zcat_data.article_simple_ean (ase_ean, ase_valid_from);
create index on zcat_data.article_simple_ean (ase_simple_sku_id);
COMMENT ON TABLE zcat_data.article_simple_ean IS 'Contains EANs for article simples. valid_from helps to build historical lists of eans<->article_simple';

