CREATE TABLE zcat_option_value.shipping_placement
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.shipping_placement (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.shipping_placement (ov_code);
