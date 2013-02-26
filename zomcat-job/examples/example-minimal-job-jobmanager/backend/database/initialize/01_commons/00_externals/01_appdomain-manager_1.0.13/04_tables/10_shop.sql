-- Draft for new shop entity, not final!
CREATE TABLE zz_commons.shop
(
  s_id                                     smallint,
  s_frontend_type                          zz_commons.shop_frontend_type NOT NULL,
  s_country                                zz_commons.country_code NOT NULL,
  s_is_active                              boolean NOT NULL DEFAULT true,
  s_code                                   varchar(255),
  s_shipping_countries                     zz_commons.country_code[],
  s_payment_methods                        varchar(50)[],
  s_stock_group_ids                        smallint[] NOT NULL,
  s_root_category_id                       integer,
  s_principal_code                         varchar(20) NOT NULL,
  s_is_statistical_article_number_required boolean NOT NULL DEFAULT false,
  -- s_is_short_sale?

  PRIMARY KEY (s_id)
);

GRANT INSERT, UPDATE ON zz_commons.shop TO robot_appdomain_updater;
