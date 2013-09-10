  CREATE TABLE zcat_option_value.material_detail
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.material_detail (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.material_detail (ov_code);
