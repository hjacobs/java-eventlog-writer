CREATE TABLE zzj_data.purchase_order_position
(
  pop_id                         serial                                      NOT NULL  PRIMARY KEY,
  pop_version                    integer                                     NOT NULL,

  pop_comment                    text                                        DEFAULT 'JUST A TESTCOMMENT',
  pop_quantity                   integer                                     DEFAULT 0,
  pop_purchase_order_id          integer                                     REFERENCES zzj_data.purchase_order (po_id)
);
