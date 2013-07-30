create table zcat_data.sku_config_counter (
    scc_model_sku       text            NOT NULL,
    scc_color_family    text            NOT NULL,
    scc_color_1         text            NOT NULL,   -- if this entry is "null" it will be "N/A"
    scc_color_2         text            NOT NULL,   -- if this entry is "null" it will be "N/A"
    scc_color_3         text            NOT NULL,   -- if this entry is "null" it will be "N/A"
    scc_material        text            NOT NULL,   -- if this entry is "null" it will be "N/A"
    scc_pattern_code    text            NOT NULL,   -- if this entry is "null" it will be "N/A"
    scc_counter         integer         NOT NULL,
    PRIMARY KEY(scc_model_sku, scc_color_1, scc_color_2, scc_color_3, scc_material, scc_pattern_code)
);
