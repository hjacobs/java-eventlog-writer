CREATE TABLE zcat_commons.plr_article_classification (
  plrac_value        text NOT NULL,
  plrac_description  text NOT NULL,
  plrac_created      timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT plr_article_classification_pkey PRIMARY KEY (plrac_value)
);
COMMENT ON TABLE zcat_commons.plr_article_classification
  IS 'Article Classification: attributes of article that led to price change';