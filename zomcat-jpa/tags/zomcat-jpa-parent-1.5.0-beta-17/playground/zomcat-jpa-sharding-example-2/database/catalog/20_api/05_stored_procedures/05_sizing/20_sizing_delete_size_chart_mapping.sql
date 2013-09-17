CREATE OR REPLACE FUNCTION sizing_delete_size_chart_mapping(
  p_brand_code text,
  p_size_chart_codes text[],
  p_commodity_group_code text,
  p_scope flow_scope
)
returns void as
$BODY$
DECLARE
    l_size_chart_group_id integer;
    l_dimension_group_id int;
    l_dimension_codes text[];
BEGIN

    l_dimension_codes := ARRAY(SELECT substr(code, 9,2) FROM unnest(p_size_chart_codes) code);
    l_dimension_group_id := sizing_get_dimension_group_id(l_dimension_codes);
    IF l_dimension_group_id IS NULL THEN
        RAISE EXCEPTION 'invalid dimension code combination %', p_size_chart_codes;
    END IF;

    SELECT size_chart_group_id INTO l_size_chart_group_id FROM sizing_create_or_get_size_chart_group_id(p_size_chart_codes, p_scope) size_chart_group_id;

    IF l_size_chart_group_id IS NULL THEN
        RAISE EXCEPTION 'Could not find size chart group for size charts: %s', p_size_chart_codes;
    END IF;

    UPDATE zcat_commons.brand_size_chart_group
       SET bscg_is_active = FALSE,
           bscg_last_modified = now(),
           bscg_last_modified_by = p_scope.user_id,
           bscg_flow_id = p_scope.flow_id
     WHERE bscg_brand_code = p_brand_code
       AND bscg_size_chart_group = l_size_chart_group_id
       AND (p_commodity_group_code IS NULL OR bscg_commodity_group_code = p_commodity_group_code)
       AND bscg_is_active = TRUE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Could not find brand size chart group mapping for brand: %s, size charts: %s, commodity group: %s', p_brand_code, p_size_chart_codes, p_commodity_group_code;
    END IF;

END
$BODY$
language plpgsql
    volatile security definer
    cost 100;