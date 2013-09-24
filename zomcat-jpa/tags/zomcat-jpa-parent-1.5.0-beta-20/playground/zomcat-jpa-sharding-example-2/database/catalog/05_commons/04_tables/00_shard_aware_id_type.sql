
CREATE TABLE zcat_commons.shard_aware_id_type (
  sait_name           text         NOT NULL PRIMARY KEY,
  sait_code           int          NOT NULL UNIQUE,
  sait_description    text         NOT NULL
);
