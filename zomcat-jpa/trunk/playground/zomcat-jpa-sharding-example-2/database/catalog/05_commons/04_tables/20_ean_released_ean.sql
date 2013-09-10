create table zcat_commons.ean_released_ean (
    ere_id                  serial          NOT NULL  PRIMARY KEY,

    ere_created             timestamptz     NOT NULL  DEFAULT now(),
    ere_created_by          text            NOT NULL,
    ere_last_modified       timestamptz     NOT NULL  DEFAULT now(),
    ere_last_modified_by    text            NOT NULL,
    ere_flow_id             text            NULL,

    ere_ean_number_range_id integer         references zcat_commons.ean_number_range (enr_id),
    ere_released_ean        EAN13           NOT NULL
);

CREATE UNIQUE INDEX ean_released_ean_number_range_id_released_ean_uidx ON zcat_commons.ean_released_ean(ere_ean_number_range_id, ere_released_ean);

COMMENT ON TABLE zcat_commons.ean_released_ean IS
'The table stores eans that have been release for re-usage.';

COMMENT ON COLUMN zcat_commons.ean_released_ean.ere_ean_number_range_id  IS 'The reference to the number range this ean belongs to.';
COMMENT ON COLUMN zcat_commons.ean_released_ean.ere_released_ean         IS 'Stores the release EAN.';
