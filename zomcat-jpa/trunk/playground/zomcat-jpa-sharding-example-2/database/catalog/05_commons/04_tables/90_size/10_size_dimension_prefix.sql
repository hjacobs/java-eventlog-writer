
CREATE TABLE zcat_commons.size_dimension_prefix (
  sdp_id                integer      NOT NULL  PRIMARY KEY,

  sdp_created           timestamptz  NOT NULL  DEFAULT now(),
  sdp_created_by        text         NOT NULL,
  sdp_last_modified     timestamptz  NOT NULL  DEFAULT now(),
  sdp_last_modified_by  text         NOT NULL,
  sdp_flow_id           text         NULL,

  sdp_name              text         NOT NULL,
  sdp_value             text         NOT NULL
);

COMMENT ON TABLE zcat_commons.size_dimension_prefix IS
  'Lookup table that declares the different ways of separating sizes of a multidimensional article.';

COMMENT ON COLUMN zcat_commons.size_dimension_prefix.sdp_name IS
  'The name of the separator type as displayed in the admin frontend.';

COMMENT ON COLUMN zcat_commons.size_dimension_prefix.sdp_value IS
  'The character(s) that separates two sizes.';

INSERT INTO zcat_commons.size_dimension_prefix
            (sdp_id, sdp_name,          sdp_value, sdp_created_by, sdp_last_modified_by)
     VALUES (0,      'NONE',            '',        'bootstrap',    'bootstrap'),
            (1,      'CROSS',           'x',       'bootstrap',    'bootstrap'),
            (2,      'SEPARATE_BUTTON', 'sp',      'bootstrap',    'bootstrap'),
            (3,      'SPACE',           ' ',       'bootstrap',    'bootstrap'),
            (4,      'SLASH',           '/',       'bootstrap',    'bootstrap');
