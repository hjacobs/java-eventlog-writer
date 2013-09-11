CREATE TABLE zcat_data.article_simple_size (
  ass_article_simple_sku_id integer                  NOT NULL  REFERENCES zcat_data.article_simple (as_simple_sku_id),

  ass_created               timestamptz              NOT NULL  DEFAULT now(),
  ass_created_by            text                     NOT NULL,
  ass_last_modified         timestamptz              NOT NULL  DEFAULT now(),
  ass_last_modified_by      text                     NOT NULL,
  ass_flow_id               text                     NOT NULL,
  ass_version               integer                  NOT NULL,

  ass_size_chart_code       text                     NOT NULL,
  ass_size_code             text                     NOT NULL,

  PRIMARY KEY (ass_article_simple_sku_id, ass_size_chart_code, ass_size_code),
  FOREIGN KEY (ass_size_chart_code, ass_size_code) REFERENCES zcat_commons.size (s_size_chart_code, s_code)
);

CREATE UNIQUE INDEX article_simple_size_article_simple_sku_id_size_chart_code_idx
ON zcat_data.article_simple_size(ass_article_simple_sku_id, ass_size_chart_code);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_simple_size'::regclass);
