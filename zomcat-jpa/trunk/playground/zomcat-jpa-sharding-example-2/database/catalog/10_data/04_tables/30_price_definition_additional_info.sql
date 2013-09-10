create table zcat_data.price_definition_additional_info (
    pdai_price_definition_id           bigint      NOT NULL PRIMARY KEY REFERENCES zcat_data.price_definition(pd_id) ON DELETE CASCADE,
    pdai_created                       timestamptz NOT NULL DEFAULT now(),
    pdai_created_by                    text        NOT NULL,
    pdai_last_modified                 timestamptz NOT NULL DEFAULT now(),
    pdai_last_modified_by              text        NOT NULL,
    pdai_flow_id                       text        NOT NULL,
    pdai_is_high_priority              boolean     NOT NULL DEFAULT false,
    pdai_original_price_definition_id  bigint      REFERENCES zcat_data.price_definition(pd_id)
);

create index on zcat_data.price_definition_additional_info (pdai_price_definition_id) where pdai_original_price_definition_id is not null;
