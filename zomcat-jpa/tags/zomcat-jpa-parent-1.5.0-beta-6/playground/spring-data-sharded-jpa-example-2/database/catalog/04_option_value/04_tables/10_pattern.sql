  CREATE TABLE zcat_option_value.pattern
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.pattern (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.pattern (ov_code);
