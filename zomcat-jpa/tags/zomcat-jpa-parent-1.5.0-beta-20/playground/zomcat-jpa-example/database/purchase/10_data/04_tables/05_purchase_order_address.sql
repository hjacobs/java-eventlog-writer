CREATE TABLE zzj_data.purchase_order_address
(
  poa_id                         serial                                      NOT NULL  PRIMARY KEY,
  poa_version                    integer                                     NOT NULL,

  poa_creation_date              timestamptz                                 NOT NULL  DEFAULT now(),
  poa_created_by                 text                                        NOT NULL,
  poa_modification_date          timestamptz                                 NOT NULL  DEFAULT now(),
  poa_modified_by                text                                        NOT NULL,

  poa_name                       text                                        DEFAULT 'NO_NAME',
  poa_street                     text                                        DEFAULT 'NO STREET'
);
