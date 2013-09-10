  CREATE TABLE zcat_option_value.leather_type
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.leather_type (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.leather_type (ov_code);
