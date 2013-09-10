CREATE TABLE zcat_option_value.tax_classification
(
) INHERITS (zcat_option_value.option_value);

CREATE UNIQUE INDEX ON zcat_option_value.tax_classification (ov_id);
CREATE UNIQUE INDEX ON zcat_option_value.tax_classification (ov_code);
