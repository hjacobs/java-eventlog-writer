BEGIN;
RESET role;
DO $$
BEGIN

  IF current_database() like 'local_%' THEN

    -- integration test user with write permissions to data and execute permissions
    PERFORM setup_create_role('zalando_integration_user',
                              ARRAY['zalando_data_writer',
                                    'zalando_backend_role'],
                              'zalando_integration_user');

    PERFORM setup_create_role('tws_zalando',ARRAY[]::text[],'pilot2go');

    GRANT tws_zalando TO zalando_integration_user;
    GRANT zalando_data_writer TO tws_zalando;

  END IF;

  -- create login roles
  IF current_database() like ANY(ARRAY['%_bm_db','local_%']) THEN

    PERFORM setup_create_role('zalando_bm_api', ARRAY['zalando_backend_role'], 'zalando_bm_api');

    PERFORM setup_create_role('robot_appdomain_updater', ARRAY['zalando_data_usage'], password :='robot_appdomain_updater');
    PERFORM setup_create_role('robot_article_review_manager',password :='robot_article_review_manager');
    PERFORM setup_create_role('robot_push_factor_import',password :='robot_push_factor_import');
    PERFORM setup_create_role('robot_table_cleaner',password :='robot_table_cleaner');
    PERFORM setup_create_role('robot_webtrekk',password :='robot_webtrekk');

    PERFORM setup_create_role('robot_quality_label_lookup', password := 'md59aa92e93edd95b34afcc0d92c3e36c43' );

    PERFORM setup_create_role('serverstats',password :='serverstats');

    PERFORM setup_create_role('zalando_monitor', ARRAY['zalando_data_reader'], 'zalando_monitor');
    PERFORM setup_create_role('zalando_erp_reader' , ARRAY['zalando_data_reader'], 'md5af40265b93a8cebe61e8aaeaad286469');
    PERFORM setup_create_role('zalando_erp_reader_nojoinopt' , ARRAY['zalando_data_reader'], 'md5af40265b93a8cebe61e8aaeaad286469');

    PERFORM setup_create_role('robot_docdata_stock_fetcher', ARRAY['zalando_api_executor','zalando_data_writer'], password := 'md5a2e762a9365a6ea85663136623d5d9cd' );
    PERFORM setup_create_role('robot_video_upload',ARRAY['zalando_api_usage'],password:='md5959fd779a75a7d484a42574ed923fe51');

  END IF;

  IF current_database() like ANY(ARRAY['%_addr_db','local_%']) THEN
    PERFORM setup_create_role('zalando_bm_api', ARRAY['zalando_backend_role'] , 'zalando_bm_api');
    PERFORM setup_create_role('zalando_monitor', ARRAY['zalando_data_reader'] , 'zalando_monitor');
    PERFORM setup_create_role('serverstats',password :='serverstats');
  END IF;

  IF current_database() like ANY(ARRAY['%_shop_db','local_%']) THEN

    PERFORM setup_create_role('robot_app_config_scheduler',password :='robot_app_config_scheduler');
    PERFORM setup_create_role('robot_appdomain_updater',ARRAY['zalando_data_usage'], password :='robot_appdomain_updater');
    PERFORM setup_create_role('robot_article_review_manager',password :='robot_article_review_manager');
    PERFORM setup_create_role('robot_push_factor_import',password :='robot_push_factor_import');
    PERFORM setup_create_role('robot_table_cleaner',password :='robot_table_cleaner');
    PERFORM setup_create_role('robot_webtrekk',password :='robot_webtrekk');
    PERFORM setup_create_role('robot_monitor_feedback',password :='robot_monitor_feedback');
    PERFORM setup_create_role('robot_monitor_stock',password :='robot_monitor_stock');
    PERFORM setup_create_role('robot_monitor_abtest',password :='robot_monitor_abtest');
    PERFORM setup_create_role('robot_query_redirect_import',password :='robot_query_redirect_import');
    PERFORM setup_create_role('robot_search_tracking_import',password :='robot_search_tracking_import');

    PERFORM setup_create_role('zalando_monitor', ARRAY['zalando_data_reader'] , 'zalando_monitor');

  END IF;

  IF current_database() LIKE ANY(ARRAY['%_customer%','local_%']) THEN

     PERFORM setup_create_role('robot_discrimination_importer', password :='robot_discrimination_importer');
     PERFORM setup_create_role('robot_erp_writer', ARRAY['zalando_api_usage'] , password := 'md5b7bb312df882a89350b7a7b61bc4d22a');

     PERFORM setup_create_role('zalando_ro', password :='zalando_ro');
     PERFORM setup_create_role('zalando_plproxy_ro', password :='zalando_plproxy_ro'); --only on shards
     PERFORM setup_create_role('zalando_monitor', ARRAY['zalando_data_reader'], password :='zalando_monitor');

  END IF;

  IF current_database() LIKE ANY(ARRAY['%partner%','local_%']) THEN
    PERFORM setup_create_role('robot_partner_queue_importer', ARRAY['zalando_partner_queue_updater'], password:='robot_partner_queue_importer');
  END IF;

  IF current_database() like 'local_%'
     OR current_database() NOT like ANY(ARRAY['%_bm_db','%_addr_db']) THEN
    PERFORM setup_create_role('zalando_api', ARRAY['zalando_backend_role'], 'zalando_api');
    PERFORM setup_create_role('robot_addr_check', password := 'md51479d401cbb99b7185c69db643bdedfc');
  END IF;

  IF current_database() LIKE 'local_%' OR current_database() LIKE '%_admin%' THEN
    PERFORM setup_create_role('zalando_admin', password := 'zalando_admin');
    GRANT zalando_admin TO zalando_zomcat_admin;
    GRANT zalando_admin TO zalando_zomcat_cct;
  END IF;

  IF current_database() LIKE 'local_%' OR current_database() LIKE '%_export%' THEN
    PERFORM setup_create_role('robot_appdomain_updater',ARRAY['zalando_data_usage'], password :='robot_appdomain_updater');
  END IF;

  IF current_database() like ANY(ARRAY['%customer%', 'local_%']) THEN
    -- will get access to discrimination importer sproc zc_api.update_customer_return_label_mode()
    PERFORM setup_create_role('robot_discrimination_importer', ARRAY['zalando_api_usage']);
  END IF;

  IF current_database() LIKE ANY(ARRAY['%testing%', '%integration%']) THEN
    PERFORM setup_create_role('robot_testing_jmeter', ARRAY['zalando_data_reader'],'md5b21c37f21023171bd2be0b748dc95b13');
  END IF;

  PERFORM setup_create_role('robot_monitor_pm', ARRAY['zalando_data_reader'], 'md59e18ec0d0d42be992cecb3fd7497f6a6' );

  PERFORM setup_create_role('robot_monitor_reconnoiter_stats', password := '18cd25d1016b'); -- no group, only catalog privileges

  PERFORM setup_create_role('robot_bi_collector', ARRAY['zalando_data_usage'], 'edVYD4FnDaOBIoIa6Eb0');

  PERFORM setup_create_role('robot_recon', password := 'md5572ed135fd657faa83c57f971047be39');

  PERFORM setup_create_role('nagios', password := 'nagios' );

  PERFORM setup_create_role('robot_24x7', ARRAY['zalando_data_reader'], 'md50f8c9430ad12c2bfb4c43b1b325c245a');

  PERFORM setup_create_role('robot_pre_auth_queue_fixer', ARRAY['zalando_data_reader'], 'md5b2f83d0e3288c3769ba0ca91bb0e7bd4' );

  -- on testing this will give write privileges to all dbs
  IF current_database() LIKE ANY(ARRAY['testing_addr_db','local_%','%addr%']) THEN
    GRANT zalando_data_writer TO robot_addr_check;
    GRANT zalando_backend_role TO robot_addr_check;
  END IF;

END;
$$;
END;
