CREATE TYPE zzj_data.order_status AS ENUM ('INITIAL','ORDERED','CHANGE');

CREATE TABLE zzj_data.purchase_order
(
  po_id                         serial                                      NOT NULL  PRIMARY KEY,
  po_version                    integer                                     NOT NULL,
  po_business_key               text                                        NOT NULL DEFAULT zzj_data.get_next_number('PURCHASE_ORDER'),
  po_order_status               zzj_data.order_status                       NOT NULL DEFAULT 'INITIAL',

  po_brand_code                 text                                        NOT NULL,
  po_creation_date              timestamptz                                 NOT NULL  DEFAULT now(),
  po_created_by                 text                                        NOT NULL,
  po_modification_date          timestamptz                                 NOT NULL  DEFAULT now(),
  po_modified_by                text                                        NOT NULL,
  po_field_without_annotation   text                                        DEFAULT 'JUST A TEST',
  po_field_with_annotation      text,
  po_is_ordered                 boolean,
  po_is_canceled                boolean                                     DEFAULT false,

  po_address_id                 integer                                     REFERENCES zzj_data.purchase_order_address (poa_id)
);
