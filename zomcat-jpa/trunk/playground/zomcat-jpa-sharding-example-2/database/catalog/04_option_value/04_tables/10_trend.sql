  CREATE TABLE zcat_option_value.trend
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.trend (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.trend (ov_code);
