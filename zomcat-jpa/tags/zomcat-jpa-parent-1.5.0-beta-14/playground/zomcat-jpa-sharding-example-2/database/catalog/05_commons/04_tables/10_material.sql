CREATE TABLE zcat_commons.material (
  m_code              text                        NOT NULL  PRIMARY KEY,

  m_created           timestamptz                 NOT NULL  DEFAULT now(),
  m_created_by        text                        NOT NULL,
  m_last_modified     timestamptz                 NOT NULL  DEFAULT now(),
  m_last_modified_by  text                        NOT NULL,
  m_flow_id           text                        NULL,

  m_type_id           int references zcat_option_value.material_type(ov_id) NOT NULL,
  m_characteristic    text                        NOT NULL,
  m_name_message_key  text                        NOT NULL,

  CONSTRAINT material_code_check CHECK (m_code ~ '^[0-9]{3}$')

);

-- CREATE UNIQUE INDEX material_type_characteristic_uidx ON zcat_data.article_model(am_type, am_characteristic);

COMMENT ON COLUMN zcat_commons.material.m_type_id
    IS 'This field is the type of the material (see option_value.MATERIAL_TYPE).';
COMMENT ON COLUMN zcat_commons.material.m_characteristic
    IS 'This field is the characteristic of the material.';
