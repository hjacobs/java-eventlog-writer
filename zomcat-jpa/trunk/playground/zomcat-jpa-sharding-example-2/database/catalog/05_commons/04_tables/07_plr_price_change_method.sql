CREATE TABLE zcat_commons.plr_price_change_method (
  plrpcm_value        text NOT NULL,
  plrpcm_description  text NOT NULL,
  plrpcm_created      timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT plr_price_change_method_pkey PRIMARY KEY (plrpcm_value)
);
COMMENT ON TABLE zcat_commons.plr_price_change_method
  IS 'Price Change Method: responsible pricing method';