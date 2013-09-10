CREATE TABLE zcat_data.article_facet_multimedia (
    afm_code            bigint                                 NOT NULL  REFERENCES zcat_data.multimedia(m_code),
    afm_shop_frontend_type    zz_commons.shop_frontend_type    NOT NULL,

    afm_created               timestamptz                      NOT NULL  DEFAULT now(),
    afm_created_by            text                             NOT NULL,
    afm_last_modified         timestamptz                      NOT NULL  DEFAULT now(),
    afm_last_modified_by      text                             NOT NULL,
    afm_flow_id               text                             NOT NULL,
    afm_version               integer                          NOT NULL,

    afm_sort_key              integer                          NOT NULL,

    PRIMARY KEY (afm_code, afm_shop_frontend_type)
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_multimedia'::regclass);
