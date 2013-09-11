create type sales_force_supplier_summary as (
    supplier_sku    text,
    supplier_color  text
);

create type sales_force_article_summary as (
    config_sku          text,
    brand               text,
    name                text,
    supplier_summaries  sales_force_supplier_summary[],
    color               text
    -- additional fields come from bm.
);
