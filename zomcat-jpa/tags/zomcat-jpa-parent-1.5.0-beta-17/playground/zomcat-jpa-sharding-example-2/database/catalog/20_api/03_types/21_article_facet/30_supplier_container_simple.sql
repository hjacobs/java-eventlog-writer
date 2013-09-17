create type supplier_container_simple as (
    version    integer,
    simple_sku text,
    suppliers supplier_simple[]
);
