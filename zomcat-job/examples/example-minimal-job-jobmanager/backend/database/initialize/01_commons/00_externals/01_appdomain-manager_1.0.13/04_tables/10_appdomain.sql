CREATE TABLE zz_commons.appdomain
(
  ad_id                                     smallint,
  ad_code                                   varchar(255),
  ad_name                                   varchar(255),
  ad_app_url                                varchar(255),
  ad_static_url                             varchar(255),
  ad_currency                               char(3),
  ad_locale                                 varchar(8),
  ad_shipping_countries                     char(2)[],
  ad_stocks                                 smallint[] NOT NULL,
  ad_root_category_id                       integer,
  ad_payment_methods                        varchar(50)[],
  ad_docdata_affiliate_id                   varchar(2) NOT NULL,
  ad_last_modified                          timestamp without time zone NOT NULL DEFAULT clock_timestamp(),
  ad_priority                               smallint NOT NULL DEFAULT 1,
  ad_appdomain_set_id                       smallint NOT NULL DEFAULT 1,
  ad_default_newsletter_id                  smallint,
  ad_customer_appdomain_set_id              smallint  NOT NULL,
  ad_is_short_sale                          boolean DEFAULT false,
  ad_is_lounge                              boolean DEFAULT false,
  ad_principal_code                         varchar(20) NOT NULL,
  ad_customer_principal_code                varchar(20) NOT NULL,
  ad_mobile_url                             varchar(255),
  ad_is_statistical_article_number_required boolean NOT NULL DEFAULT false,
  PRIMARY KEY (ad_id)
);

COMMENT ON TABLE zz_commons.appdomain IS 'Hier werden verschiedene Applikations definiert. Eine Appdomain kann ein Land (z.B. www.zalando.de), ein Whitelabel-Instanz oder eine eigene Applikations-Instanz.

Pro Applikations-Instanz können Werte wie URLs, Währungen, Locales, etc. (in zusätzlichen Spalten) festgelegt werden.';
COMMENT ON COLUMN zz_commons.appdomain.ad_app_url IS 'Die URL der Applikation';
COMMENT ON COLUMN zz_commons.appdomain.ad_mobile_url IS 'Die URL der mobilen Site';

ALTER TABLE zz_commons.appdomain OWNER TO zalando;

-- GRANT SELECT ON TABLE zz_commons.appdomain TO robot_push_factor_import;
GRANT INSERT, UPDATE ON zz_commons.appdomain TO robot_appdomain_updater;
