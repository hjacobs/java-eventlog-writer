  CREATE TABLE zcat_option_value.shoe_lining_material
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.shoe_lining_material (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.shoe_lining_material (ov_code);
