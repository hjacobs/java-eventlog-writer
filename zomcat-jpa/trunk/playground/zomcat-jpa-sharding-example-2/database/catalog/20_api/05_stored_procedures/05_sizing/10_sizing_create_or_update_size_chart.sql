CREATE OR REPLACE FUNCTION sizing_create_or_update_size_chart(
  p_size_chart  size_chart,
  p_scope       flow_scope
)
  RETURNS void AS
$BODY$
/*
-- $Id$
-- $HeadURL$

-- test

  set search_path=zcat_api_r12_00_40;
  SELECT * FROM create_model_sku ('10K11A008');
*/
DECLARE
  l_code varchar(10);
  l_brand_code varchar(3);
  l_dimension_code varchar(2);
BEGIN
    l_code := p_size_chart.code;

    RAISE INFO 'sizing_create_or_update_size_chart %', p_size_chart.sizes;

    IF length(p_size_chart.code) != 10 THEN
      RAISE EXCEPTION 'size chart code must have 10 digits. given code is "%"', l_code;
    END IF;

    l_brand_code := substr(l_code, 5, 3);
    l_dimension_code := substr(l_code, 9, 2);

    IF l_brand_code != '000' AND NOT EXISTS(
        select null
          from zcat_commons.brand
         where b_code = l_brand_code
    ) THEN
      RAISE EXCEPTION 'size chart code % refers to an unknown brand code %', l_code, l_brand_code;
    END IF;

    IF NOT EXISTS(
        select null
          from zcat_commons.size_dimension
         where sd_code = l_dimension_code
    ) THEN
      RAISE EXCEPTION 'size chart code % refers to an unknown dimension code %', l_code, l_dimension_code;
    END IF;

    UPDATE zcat_commons.size_chart
       SET sc_description_message_key = p_size_chart.description_message_key,
           sc_last_modified = now(),
           sc_last_modified_by = p_scope.user_id,
           sc_flow_id = p_scope.flow_id
     WHERE sc_code = l_code;

    IF NOT FOUND THEN
        INSERT
          INTO zcat_commons.size_chart (
                 sc_code,
                 sc_brand_code,
                 sc_dimension_code,
                 sc_description_message_key,
                 sc_created_by,
                 sc_last_modified_by,
                 sc_flow_id
               )
        VALUES (
                 l_code,
                 CASE l_brand_code WHEN '000' THEN null ELSE l_brand_code END,
                 l_dimension_code,
                 p_size_chart.description_message_key,
                 p_scope.user_id,
                 p_scope.user_id,
                 p_scope.flow_id
               );

    END IF;

    UPDATE zcat_commons.size
       SET s_supplier_size      = trim(u.supplier_size),
           s_value              = u.size_value,
           s_sort_key           = u.sort_key,
           s_last_modified      = now(),
           s_last_modified_by   = p_scope.user_id,
           s_flow_id            = p_scope.flow_id
      FROM unnest(p_size_chart.sizes) u
     WHERE s_code = (u.code).size_code
       AND s_size_chart_code = p_size_chart.code;

    INSERT
      INTO zcat_commons.size (
               s_size_chart_code,
               s_code,
               s_supplier_size,
               s_sort_key,
               s_value,
               s_created_by,
               s_last_modified_by,
               s_flow_id
           )
             select l_code,
                    (u.code).size_code,
                    trim(u.supplier_size),
                    u.sort_key,
                    u.size_value,
                    p_scope.user_id,
                    p_scope.user_id,
                    p_scope.flow_id
               from unnest(p_size_chart.sizes) u
               left
               join zcat_commons.size
                 on s_size_chart_code = l_code
                and s_code = (u.code).size_code
              where s_code is null;

END;
$BODY$
  LANGUAGE 'plpgsql' VOLATILE SECURITY DEFINER
  COST 100;
