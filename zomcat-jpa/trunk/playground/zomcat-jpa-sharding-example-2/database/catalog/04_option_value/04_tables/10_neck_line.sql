CREATE TABLE zcat_option_value.neck_line
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.neck_line (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.neck_line (ov_code);
