CREATE OR REPLACE FUNCTION article_get_supplier_sku_for_model (
    p_model_sku     text,
    p_supplier_code text
) returns text AS

$BODY$
DECLARE

    l_supplier_sku text;

BEGIN

    select code
    into l_supplier_sku
    from (
        select distinct coalesce (afsm_article_code, coalesce (afsc_article_code, afss_article_code)) as code, t_skus.as_sku_type
        from zcat_data.article_sku t_model
            join zcat_data.article_sku t_skus on t_model.as_id in (t_skus.as_model_id, t_skus.as_id)
            left join zcat_data.article_model model on t_skus.as_id = model.am_model_sku_id
            left join zcat_data.article_config config on t_skus.as_id = config.ac_config_sku_id
            left join zcat_data.article_simple simple on t_skus.as_id = simple.as_simple_sku_id
            left join zcat_data.article_facet_supplier_simple on afss_simple_sku_id = simple.as_simple_sku_id and afss_supplier_code = p_supplier_code
            left join zcat_data.article_facet_supplier_config on afsc_config_sku_id = config.ac_config_sku_id and afsc_supplier_code = p_supplier_code
            left join zcat_data.article_facet_supplier_model on afsm_model_sku_id = model.am_model_sku_id and afsm_supplier_code = p_supplier_code
        where t_model.as_sku = p_model_sku
            and t_model.as_sku_type = 'MODEL'
        order by t_skus.as_sku_type desc
    ) t
    where code is not null
    limit 1;

    return l_supplier_sku;
END;

$BODY$

LANGUAGE plpgsql VOLATILE SECURITY DEFINER;


