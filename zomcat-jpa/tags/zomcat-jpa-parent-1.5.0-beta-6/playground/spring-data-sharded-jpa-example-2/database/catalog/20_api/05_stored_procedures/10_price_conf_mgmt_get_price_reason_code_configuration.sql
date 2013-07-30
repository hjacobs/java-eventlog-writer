CREATE OR REPLACE FUNCTION price_conf_mgmt_get_price_reason_code_configuration (
) RETURNS plr_code_configurations AS
$BODY$

/** -- test
show search_path;

set search_path to zcat_api_r13_00_18, public;
select * from price_conf_mgmt_get_price_reason_code_configuration();
*/

DECLARE
    l_plr_code_configurations   plr_code_configurations;
    l_article_classification    plr_article_classification[];
    l_price_change_information  plr_price_change_information[];
    l_price_change_method       plr_price_change_method[];

BEGIN

    l_article_classification := ARRAY(
        SELECT x::plr_article_classification FROM ( SELECT plrac_value, plrac_description FROM zcat_commons.plr_article_classification ) x);
    raise info 'article classification = % ', l_article_classification;

    l_price_change_information := ARRAY(
        SELECT x::plr_price_change_information FROM ( SELECT plrpci_value, plrpci_description FROM zcat_commons.plr_price_change_information ) x);
    raise info 'price change information = % ', l_price_change_information;

    l_price_change_method := ARRAY(
        SELECT x::plr_price_change_method FROM ( SELECT plrpcm_value, plrpcm_description FROM zcat_commons.plr_price_change_method ) x);
    raise info 'price change method = % ', l_price_change_method;

    l_plr_code_configurations.plr_article_classification    := l_article_classification;
    l_plr_code_configurations.plr_price_change_information  := l_price_change_information;
    l_plr_code_configurations.plr_price_change_method       := l_price_change_method;

    RETURN l_plr_code_configurations;
END
$BODY$

LANGUAGE plpgsql
  VOLATILE
  SECURITY DEFINER
  COST 100;