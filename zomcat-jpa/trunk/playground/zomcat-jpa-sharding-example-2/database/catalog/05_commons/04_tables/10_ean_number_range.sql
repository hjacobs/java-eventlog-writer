create table zcat_commons.ean_number_range (
    enr_id                      serial                              NOT NULL  PRIMARY KEY,

    enr_created                 timestamptz                         NOT NULL  DEFAULT now(),
    enr_created_by              text                                NOT NULL,
    enr_last_modified           timestamptz                         NOT NULL  DEFAULT now(),
    enr_last_modified_by        text                                NOT NULL,
    enr_flow_id                 text                                NULL,

    enr_name                    text                                NOT NULL  UNIQUE,
    enr_active                  boolean                             NOT NULL,
    enr_ean_number_range_type   zcat_commons.ean_number_range_type  NOT NULL,
    enr_start_ean               EAN13                               NOT NULL,
    enr_end_ean                 EAN13                               NOT NULL,
    enr_increase_by             integer                             NOT NULL,
    enr_last_ean                EAN13                               NULL
);

COMMENT ON TABLE zcat_commons.ean_number_range IS
'The table stores number ranges. A number range can by of type (ean_number_range_type),
 currently only ZALANDO and DUMMY.
 A range is defined by a enr_start and enr_end EAN and an increase number enr_increase_by
 which is used to count. The last return ean is stored in enr_last_ean.';

COMMENT ON COLUMN zcat_commons.ean_number_range.enr_name                    IS 'The name of the number range. Must be unique.';
COMMENT ON COLUMN zcat_commons.ean_number_range.enr_active                  IS 'Set to true is the number range can be used.';
COMMENT ON COLUMN zcat_commons.ean_number_range.enr_ean_number_range_type   IS 'Set to ZALANDO or DUMMY';
COMMENT ON COLUMN zcat_commons.ean_number_range.enr_start_ean               IS 'Stores the first valid EAN of the number range.';
COMMENT ON COLUMN zcat_commons.ean_number_range.enr_end_ean                 IS 'Stores the last valid EAN of the number range.';
COMMENT ON COLUMN zcat_commons.ean_number_range.enr_increase_by             IS 'Stores the counter for the number range. Different from if some static parts are defined.';
COMMENT ON COLUMN zcat_commons.ean_number_range.enr_last_ean                IS 'Stores the last returned EAN.';
