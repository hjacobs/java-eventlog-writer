CREATE TABLE zcat_commons.commodity_group (
  cg_code                  text          NOT NULL  PRIMARY KEY,

  cg_created               timestamptz   NOT NULL  DEFAULT now(),
  cg_created_by            text          NOT NULL,
  cg_last_modified         timestamptz   NOT NULL  DEFAULT now(),
  cg_last_modified_by      text          NOT NULL,
  cg_flow_id               text          NULL,

  cg_parent_code           text          NULL      REFERENCES zcat_commons.commodity_group(cg_code),
  cg_name_message_key      text          NOT NULL,
  cg_dd_sub_product_group  character(4)  NULL      DEFAULT NULL::bpchar, -- TODO whats that
  cg_is_active             boolean       NOT NULL  DEFAULT true,

  CONSTRAINT commodity_group_dd_sub_product_group_check
       CHECK (cg_dd_sub_product_group = ANY ('{3000,3001,3010,3011,3012,3020,3021,3022,3030,3031,
                                               3032,3040,3041,3042,3050,3051,3052,3064,3065}'::bpchar[]))
);

COMMENT ON TABLE zcat_commons.commodity_group IS 'The table stores commodity groups, which are known as article types in BM, and called Warengruppe in german.';

COMMENT ON COLUMN zcat_commons.commodity_group.cg_code IS 'The code is the same as in BM. It is a tree structure, where each code contains the parent as a prefix.';
COMMENT ON COLUMN zcat_commons.commodity_group.cg_parent_code IS 'code to the parent of this commodity group. All roots have NULL as an code.';
COMMENT ON COLUMN zcat_commons.commodity_group.cg_name_message_key IS 'Name of the group as message key.';
COMMENT ON COLUMN zcat_commons.commodity_group.cg_dd_sub_product_group IS 'TODO describe this.';

INSERT INTO zcat_commons.commodity_group
            (cg_code,   cg_parent_code, cg_name_message_key,            cg_created_by,  cg_last_modified_by)
     VALUES ('100',       null,           'key.todo.root-node-1',         'bootstrap',    'bootstrap'),
            ('200',       null,           'key.todo.root-node-2',         'bootstrap',    'bootstrap'),
            ('100-1',     '100',          'key.todo.child-node-1-1',      'bootstrap',    'bootstrap'),
            ('100-2',     '100',          'key.todo.child-node-1-2',      'bootstrap',    'bootstrap'),
            ('100-3',     '100',          'key.todo.child-node-1-3',      'bootstrap',    'bootstrap'),
            ('200-1',     '200',          'key.todo.child-node-2-1',      'bootstrap',    'bootstrap'),
            ('200-2',     '200',          'key.todo.child-node-2-2',      'bootstrap',    'bootstrap'),
            ('100-1-1',   '100-1',        'key.todo.child-node-1-1-1',    'bootstrap',    'bootstrap');


