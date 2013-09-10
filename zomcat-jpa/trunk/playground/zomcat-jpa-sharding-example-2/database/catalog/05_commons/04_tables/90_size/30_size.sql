
CREATE TABLE zcat_commons.size (
  s_created           timestamptz              NOT NULL  DEFAULT now(),
  s_created_by        text                     NOT NULL,
  s_last_modified     timestamptz              NOT NULL  DEFAULT now(),
  s_last_modified_by  text                     NOT NULL,
  s_flow_id           text                     NULL,
  s_size_chart_code   text                     NOT NULL  REFERENCES zcat_commons.size_chart(sc_code),
  s_code              text                     NOT NULL,
  s_supplier_size     text                     NOT NULL,
  s_sort_key          integer                  NOT NULL,
  s_value             zcat_commons.size_value  NOT NULL,
  CONSTRAINT size_code_check CHECK (s_code ~ '^[A-Z0-9]{1,3}$'),
  PRIMARY KEY (s_size_chart_code, s_code)
);

CREATE UNIQUE INDEX size_code_and_chart_code_uidx
ON zcat_commons.size(s_size_chart_code, s_code);

CREATE UNIQUE INDEX size_size_chart_code_sort_key_uidx
ON zcat_commons.size(s_size_chart_code, s_sort_key);

COMMENT ON TABLE zcat_commons.size IS '
This table contains sizes belonging to a certain size chart. Each size has one default code
and a couple of values (see also type zcat_commons.size_value). There is one value EU, UK, US, FR and IT,
but the US value is optional.

Example:
 ------------------------------------------------------------------------------
| s_id  | s_size_chart_code | s_code | s_sort_key | s_value                    |
|------------------------------------------------------------------------------|
| 1     | 4FA1AD5X2A        | 32     | 1          |("32","6","XS","34","38")   |
| 2     | 4FA1AD5X2A        | 36     | 2          |("36","10","S","38","42")   |
| 3     | 4FA1AD5X2A        | 40     | 3          |("40","14","M","42","46")   |
| 4     | 4FA1AD5X2A        | 44     | 4          |("44","18","L","46","50")   |
| 5     | 4FA1AD5X2A        | 48     | 5          |("48","22","XL","50","54")  |
| 6     | 1MN4NI2X0A        | 10     | 1          |("44","9",,"44","44")       |
| 7     | 1MN4NI2X0A        | 11     | 2          |("45","10",,"45","45")      |
| 8     | 1MN4NI2X0A        | 12     | 3          |("46","11",,"46","46")      |
| 9     | 1MN4NI2X0A        | 13     | 4          |("47,5","12",,"47,5","47,5")|
| 10    | 1MN4NI2X0A        | 14     | 5          |("48,5","13",,"48,5","48,5")|
| 11    | 1MN4NI2X0A        | 15     | 6          |("49,5","14",,"49,5","49,5")|
 ------------------------------------------------------------------------------
 ';

COMMENT ON COLUMN zcat_commons.size.s_code IS '
a 3 digit code which is unique within the size chart.
this code is used for generating parts of the sku.';

COMMENT ON COLUMN zcat_commons.size.s_supplier_size IS '
name of the size as given by the supplier';

COMMENT ON COLUMN zcat_commons.size.s_sort_key IS '
Is used to bring the sizes of one size chart to the appropriate order. E.g. XS, S, M, L, XL,...';

COMMENT ON COLUMN zcat_commons.size.s_value IS 'Contains the values (display names) of a size for EU, UK, US, FR and IT.';
