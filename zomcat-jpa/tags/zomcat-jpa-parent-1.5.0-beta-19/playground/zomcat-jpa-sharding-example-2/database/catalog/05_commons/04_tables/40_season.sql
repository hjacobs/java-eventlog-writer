
CREATE TABLE zcat_commons.season (
  s_code                  text          NOT NULL  PRIMARY KEY,

  s_created               timestamptz   NOT NULL  DEFAULT now(),
  s_created_by            text          NOT NULL,
  s_last_modified         timestamptz   NOT NULL  DEFAULT now(),
  s_last_modified_by      text          NOT NULL,
  s_flow_id               text          NULL,

  s_name_message_key      text          NOT NULL,
  s_is_deleted            boolean       NOT NULL  DEFAULT false,
  s_is_basics             boolean       NOT NULL  DEFAULT false,
  s_sort_key              integer       NOT NULL,
  s_active_from           timestamptz   NULL,
  s_active_to             timestamptz   NULL,

  -- season codes, except the basic seasons, must have the format FS11, HW12, HW13,...
  CONSTRAINT season_code_format_check CHECK (s_is_basics = TRUE OR s_code ~ '^(FS|HW)[0-9]{2}$')

);

COMMENT ON COLUMN zcat_commons.season.s_sort_key IS 'is used to bring the seasons in chronological order,
                                                     e.g. FS12, HW12, FS13, HW13,...';

INSERT INTO zcat_commons.season
            (s_code,                 s_name_message_key, s_is_basics, s_sort_key, s_active_from, s_active_to,  s_created_by, s_last_modified_by)
     VALUES ('YEAR_ROUND_BASICS',    'unknown',          true,        '1',        null,          null,         'bootstrap',  'bootstrap'),
            ('SPRING_SUMMER_BASICS', 'unknown',          true,        '2',        null,          null,         'bootstrap',  'bootstrap'),
            ('AUTUMN_WINTER_BASICS', 'unknown',          true,        '3',        null,          null,         'bootstrap',  'bootstrap'),
            ('HW08',                 'unknown',          false,       '101',      '2008-07-01' , '2008-12-31', 'bootstrap',  'bootstrap'),
            ('FS09',                 'unknown',          false,       '102',      '2009-01-01' , '2009-06-30', 'bootstrap',  'bootstrap'),
            ('HW09',                 'unknown',          false,       '103',      '2009-07-01' , '2009-12-31', 'bootstrap',  'bootstrap'),
            ('FS10',                 'unknown',          false,       '104',      '2010-01-01' , '2010-06-30', 'bootstrap',  'bootstrap'),
            ('HW10',                 'unknown',          false,       '105',      '2010-07-01' , '2010-12-31', 'bootstrap',  'bootstrap'),
            ('FS11',                 'unknown',          false,       '106',      '2011-01-01' , '2011-06-30', 'bootstrap',  'bootstrap'),
            ('HW11',                 'unknown',          false,       '107',      '2011-07-01' , '2011-12-31', 'bootstrap',  'bootstrap'),
            ('FS12',                 'unknown',          false,       '108',      '2012-01-01' , '2012-06-30', 'bootstrap',  'bootstrap'),
            ('HW12',                 'unknown',          false,       '109',      '2012-07-01' , '2012-12-31', 'bootstrap',  'bootstrap'),
            ('FS13',                 'unknown',          false,       '110',      '2013-01-01' , '2013-06-30', 'bootstrap',  'bootstrap'),
            ('HW13',                 'unknown',          false,       '111',      '2013-07-01' , '2013-12-31', 'bootstrap',  'bootstrap');