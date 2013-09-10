CREATE TABLE zcat_option_value.sub_season
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.sub_season (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.sub_season (ov_code);
