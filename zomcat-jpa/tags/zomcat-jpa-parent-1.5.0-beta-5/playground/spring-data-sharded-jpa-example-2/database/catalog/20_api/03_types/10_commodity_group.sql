create type commodity_group as (
    commodity_group_code        text,
    parent_Commodity_group_code text,
    name_message_key            text,
    dd_sub_product_group        character(4),
    active                      boolean,
    child_commodity_group_codes text[]
);

