CREATE TABLE zcat_data.article_facet_sales_model (
  afsm_model_sku_id         integer                        NOT NULL  PRIMARY KEY REFERENCES zcat_data.article_model(am_model_sku_id),

  afsm_created              timestamptz                    NOT NULL  DEFAULT now(),
  afsm_created_by           text                           NOT NULL,
  afsm_last_modified        timestamptz                    NOT NULL  DEFAULT now(),
  afsm_last_modified_by     text                           NOT NULL,
  afsm_flow_id              text                           NOT NULL,
  afsm_version              integer                        NOT NULL,

  afsm_is_extra_large       boolean                        NOT NULL  DEFAULT true,

  afsm_fitting_id           int references zcat_option_value.fitting(ov_id) NULL,
  afsm_closure_id           int references zcat_option_value.closure(ov_id) NULL,
  afsm_toe_cap_id           int references zcat_option_value.toe_cap(ov_id) NULL,
  afsm_sleeve_type_id       int references zcat_option_value.sleeve_type(ov_id) NULL,
  afsm_heel_height          integer                                         NULL,
  afsm_heel_type_id         int references zcat_option_value.heel_type(ov_id) NULL,
  afsm_leg_type_id          int references zcat_option_value.leg_type(ov_id) NULL,
  afsm_neck_line_id         int references zcat_option_value.neck_line(ov_id) NULL,
  afsm_shoe_upper_id        int references zcat_option_value.shoe_upper(ov_id) NULL,
  afsm_textile_membrane_id  int references zcat_option_value.textile_membrane(ov_id) NULL,
  afsm_fit_type_id          int references zcat_option_value.fit_type(ov_id) NULL,
  afsm_sport_type_id        int references zcat_option_value.sport_type(ov_id) NULL,
  afsm_sub_sport_type_id    int references zcat_option_value.sub_sport_type(ov_id) NULL

  CONSTRAINT article_facet_sales_model_sku_id_check CHECK (zcat_data.is_model_sku_id(afsm_model_sku_id)),
  CONSTRAINT article_facet_sales_model_heel_height_check CHECK (afsm_heel_height IS NULL OR afsm_heel_height BETWEEN 0 AND 15)
);

SELECT zcat_commons.create_optimistic_locking_version_trigger('zcat_data.article_facet_sales_model'::regclass);

COMMENT ON COLUMN zcat_data.article_facet_sales_model.afsm_fitting_id
     IS 'If NULL the article runs true to size, otherwise it is either smaller or larger than expected';
COMMENT ON COLUMN zcat_data.article_facet_sales_model.afsm_heel_height IS 'Between 0cm and 15cm';
