CREATE TABLE zcat_commons.plr_price_change_information (
  plrpci_value        text NOT NULL,
  plrpci_description  text NOT NULL,
  plrpci_created      timestamp without time zone NOT NULL DEFAULT now(),
  CONSTRAINT plr_price_change_information_pkey PRIMARY KEY (plrpci_value)
);
COMMENT ON TABLE zcat_commons.plr_price_change_information
  IS 'Price Change Information: exact competitor marketing channel process step etc.';