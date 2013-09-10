  CREATE TABLE zcat_option_value.textile_lining_material
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.textile_lining_material (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.textile_lining_material (ov_code);
