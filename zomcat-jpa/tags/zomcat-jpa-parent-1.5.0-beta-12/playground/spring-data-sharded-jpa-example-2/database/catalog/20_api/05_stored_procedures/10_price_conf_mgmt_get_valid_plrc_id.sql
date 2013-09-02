CREATE OR REPLACE FUNCTION price_conf_mgmt_get_valid_plrc_id(p_plr_code text)
    RETURNS text AS $$

/*
-- $Id:$
-- $HeadURL:$
*/

/**
 * validates the given price level reason code. when a price level reason comes either through a CSV-Upload or the
 * Price Overview Screen, it is validaded if the code is already in the price_level_reason table.
 * if yes, the PLR is valid, the appropriate value in price_level_reason_code is referred to the price definition
 * if not, the validation has to be done based on price_level_reason_code with all functions and options of PLR
 *  - if the PLR is valid, a new entry is created within the price_level_reason_code table as a reference for the PD
 *  - if the PLR is not valid, it throws an exception
 *
 * @ExpectedExecutionTime 20ms
 * @ExpectedExecutionFrequency EveryMinute
 */
/* --testing

  begin;
    -- set search_path to zcat_api_r13_00_13, public;

    select price_conf_mgmt_get_valid_plrc_id(null);    -- will throw an exception
    select price_conf_mgmt_get_valid_plrc_id('12300');   -- will throw an exception
    select price_conf_mgmt_get_valid_plrc_id('1234500'); -- will throw an exception
    select price_conf_mgmt_get_valid_plrc_id('2011000'); -- will return a valid plrc_id

  rollback;
*/

DECLARE
    l_method                  text;
    l_information             text;
    l_classification          text;

    l_valid_method            text;
    l_valid_information       text;
    l_valid_classification    text;

    l_plrc_id                 int;
    l_plrc_value              text;
BEGIN

    RAISE INFO 'parameter p_plr_code: %', p_plr_code;

    IF p_plr_code IS NULL THEN
        RAISE EXCEPTION 'p_plr_code is % but required', p_plr_code;
    END IF;

    IF length(p_plr_code) < 7 THEN
        RAISE EXCEPTION 'the parameter p_plr_code must be at least 7 characters';
    END IF;

    l_method := substr(p_plr_code, 1, 1);
    l_information := substr(p_plr_code, 2, 3);
    l_classification := substr(p_plr_code, 5, 1);

    RAISE INFO 'to test: method: %, information: %, classification: %', l_method, l_information, l_classification;

    l_valid_method := (SELECT plrpcm_value FROM zcat_commons.plr_price_change_method WHERE plrpcm_value = l_method);
    l_valid_information := (SELECT plrpci_value FROM zcat_commons.plr_price_change_information WHERE plrpci_value = l_information);
    l_valid_classification := (SELECT plrac_value FROM zcat_commons.plr_article_classification WHERE plrac_value = l_classification);

    RAISE INFO 'found: method: %, information: %, classification: %', l_valid_method, l_valid_information, l_valid_classification;

    IF l_valid_method IS NULL OR l_valid_information IS NULL OR l_valid_classification IS NULL THEN
      RAISE EXCEPTION '% is an unvalid price level reason code',
        COALESCE(l_valid_method, l_method) ||
        COALESCE(l_valid_information, l_information) ||
        COALESCE(l_valid_classification, l_classification);
    END IF;

    l_plrc_value := l_valid_method || l_valid_information || l_valid_classification || '00';

    -- search for existing value
    SELECT plrc_id INTO l_plrc_id
      FROM zcat_data.price_level_reason_code
     WHERE plrc_value = l_plrc_value;

    IF NOT FOUND THEN
      RAISE INFO 'create new plrc_value: %', l_plrc_value;
      INSERT INTO zcat_data.price_level_reason_code(plrc_value)
           VALUES (l_plrc_value)
        RETURNING plrc_id INTO l_plrc_id;
    END IF;

    RAISE INFO 'return plrc_id: %', l_plrc_id;

    RETURN l_plrc_id;
END;
$$ LANGUAGE plpgsql VOLATILE SECURITY DEFINER;