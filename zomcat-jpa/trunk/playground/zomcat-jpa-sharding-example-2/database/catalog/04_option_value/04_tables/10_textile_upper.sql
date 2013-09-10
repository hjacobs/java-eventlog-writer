CREATE TABLE zcat_option_value.textile_upper
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.textile_upper (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.textile_upper (ov_code);
