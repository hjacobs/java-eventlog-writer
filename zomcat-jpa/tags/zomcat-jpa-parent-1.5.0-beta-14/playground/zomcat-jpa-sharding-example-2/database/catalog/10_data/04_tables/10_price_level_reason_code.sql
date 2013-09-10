CREATE TABLE zcat_data.price_level_reason_code (
    plrc_id         serial NOT NULL,
    plrc_created    timestamptz NOT NULL DEFAULT now(),
    plrc_value      text NOT NULL,
    CONSTRAINT price_level_reason_code_pkey PRIMARY KEY (plrc_id)
);