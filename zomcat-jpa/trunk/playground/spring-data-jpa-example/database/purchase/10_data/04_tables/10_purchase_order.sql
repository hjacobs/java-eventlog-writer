CREATE TYPE zzj_data.order_status AS ENUM ('INITIAL','ORDERED','CHANGE');

CREATE TABLE zzj_data.purchase_order
(
  po_id                         serial                                      NOT NULL  PRIMARY KEY,
  po_version                    integer                                     NOT NULL,
  po_number                     text                                        NOT NULL DEFAULT zpu_data.get_next_number('PURCHASE_ORDER'),
  po_status                     zpu_data.order_status                       NOT NULL DEFAULT 'INITIAL',

  po_creation_date              timestamptz                                 NOT NULL  DEFAULT now(),
  po_created_by                 text                                        NOT NULL,
  po_modifcation_date           timestamptz                                 NOT NULL  DEFAULT now(),
  po_modified_by                text                                        NOT NULL
);

