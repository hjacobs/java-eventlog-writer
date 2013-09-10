CREATE TABLE zcat_option_value.textile_membrane
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.textile_membrane (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.textile_membrane (ov_code);
