CREATE TYPE zzj_data.number_range_type AS ENUM (
    'PARTNER', 'SUPPLIER_FAMILY', 'CONDITION_AGREEMENT', 'PPO', 'PURCHASE_ORDER'
);

CREATE TABLE zzj_data.number_range
(
    nr_type      zzj_data.number_range_type  NOT NULL  PRIMARY KEY,
    nr_seq_name  text                        NOT NULL,
    nr_prefix    text,
    nr_length    smallint
);