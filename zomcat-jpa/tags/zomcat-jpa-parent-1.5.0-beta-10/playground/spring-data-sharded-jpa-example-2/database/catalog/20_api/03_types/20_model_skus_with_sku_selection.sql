CREATE TYPE model_skus_with_sku_selection AS (
    model_skus text[],
    selected_skus int[]
);