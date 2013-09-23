CREATE TABLE zcat_data.article_simple_size_info (
    assi_article_simple_sku_id     integer                 NOT NULL  REFERENCES zcat_data.article_simple (as_simple_sku_id),

    assi_created                   timestamptz             NOT NULL  DEFAULT now(),
    assi_created_by                text                    NOT NULL,
    assi_last_modified             timestamptz             NOT NULL  DEFAULT now(),
    assi_last_modified_by          text                    NOT NULL,
    assi_flow_id                   text                    NOT NULL,

    assi_dimension_count           int                     NOT NULL,

    assi_size_display1_eu          text                    NOT NULL,
    assi_size_display2_eu          text,
    assi_size_display3_eu          text,
    assi_displayed_size_eu         text                    NOT NULL,

    assi_size_display1_uk          text,
    assi_size_display2_uk          text,
    assi_size_display3_uk          text,
    assi_displayed_size_uk         text,

    assi_size_display1_us          text,
    assi_size_display2_us          text,
    assi_size_display3_us          text,
    assi_displayed_size_us         text,

    assi_size_display1_fr          text,
    assi_size_display2_fr          text,
    assi_size_display3_fr          text,
    assi_displayed_size_fr         text,

    assi_size_display1_it          text,
    assi_size_display2_it          text,
    assi_size_display3_it          text,
    assi_displayed_size_it         text,

    assi_supplier_size1            text                    NOT NULL,
    assi_supplier_size2            text,
    assi_supplier_size3            text,
    assi_displayed_supplier_size   text                    NOT NULL,

    assi_sort_key1                 integer                 NOT NULL,
    assi_sort_key2                 integer,
    assi_sort_key3                 integer,

    PRIMARY KEY (assi_article_simple_sku_id)
);
