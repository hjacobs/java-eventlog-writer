  CREATE TABLE zcat_option_value.type_q
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.type_q (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.type_q (ov_code);
