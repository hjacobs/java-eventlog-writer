-- create default roles
BEGIN;
  RESET role;
  -- zalando is owner of *_data or in general all data schemas
  SELECT setup_create_role('zalando');

  -- all data schemas grant usage to zalando_data_usage
  -- GRANT USAGE ON SCHEMA ... TO zalando_data_usage
  SELECT setup_create_role('zalando_data_usage');

  -- all data schemas grant select on table and sequences to data_reader
  -- GRANT SELECT ON ALL TABLES IN SCHEMA ... TO zalando_data_reader;
  -- GRANT SELECT ON ALL SEQUENCES IN SCHEMA ... TO zalando_data_reader;
  SELECT setup_create_role('zalando_data_reader', ARRAY['zalando_data_usage']);

  -- gain insert,delete,update and sequence usage on all data schemas
  -- GRANT INSERT, DELETE, UPDATE ON ALL TABLES IN SCHEMA ... TO zalando_data_writer;
  -- GRANT USAGE ON ALL SEQUENCES IN SCHEMA ... TO zalando_data_writer;
  SELECT setup_create_role('zalando_data_writer', ARRAY['zalando_data_reader']);

  -- owner of all api functions
  SELECT setup_create_role('zalando_api_owner', ARRAY['zalando_data_writer']);

  -- usage for api schemas
  SELECT setup_create_role('zalando_api_usage');

  -- api executor gains EXECUTE permission to all functions in *_api schema
  -- GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA ..._api TO zalando_api_executor;
  SELECT setup_create_role('zalando_api_executor', ARRAY['zalando_api_usage']);

  SELECT setup_create_role('zalando_quartz_writer');
  SELECT setup_create_role('zalando_bm_archive_writer');

  SELECT setup_create_role('zalando_backend_role', ARRAY['zalando_api_executor',
                                                         'zalando_quartz_writer',
                                                         'zalando_bm_archive_writer']);

  SELECT setup_create_role('zalando_partner_queue_updater', ARRAY['zalando_data_usage']);

  -- the following roles are used to distinguish privileges depending on where the login is coming from
  -- the suffix indicates the connection's origin
  SELECT setup_create_role('zalando_zomcat_shop');
  SELECT setup_create_role('zalando_zomcat_bm');
  SELECT setup_create_role('zalando_zomcat_customer');
  SELECT setup_create_role('zalando_zomcat_admin');
  SELECT setup_create_role('zalando_zomcat_user');
  SELECT setup_create_role('zalando_zomcat_cms');
  SELECT setup_create_role('zalando_zomcat_cct');
  SELECT setup_create_role('zalando_zomcat_export');
  SELECT setup_create_role('zalando_zomcat_partner');
  SELECT setup_create_role('zalando_zomcat_catalog');
  SELECT setup_create_role('zalando_zomcat_acs');
  SELECT setup_create_role('zalando_zomcat_stock');
  SELECT setup_create_role('zalando_zomcat_orderengine');
  SELECT setup_create_role('zalando_zomcat_payment');
  SELECT setup_create_role('zalando_zomcat_logistics');
  SELECT setup_create_role('zalando_zomcat_wh');
  SELECT setup_create_role('zalando_zomcat_purchase');
  SELECT setup_create_role('zalando_zomcat_pricecrawler');

  -- role for dblink partner access
  SELECT setup_create_role('zalando_queue_dblink_usage');
  -- role for dblink zalos and zalos-dwh access
  SELECT setup_create_role('zalando_zalos_reader');
  SELECT setup_create_role('zalando_zalosdwh_reader');

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_bm%','%_addr%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_bm;
    GRANT zalando_backend_role TO zalando_zomcat_shop;
    GRANT zalando_backend_role TO zalando_zomcat_export;

    -- gives access to dblinks for partner service ( queues )
    GRANT zalando_queue_dblink_usage TO zalando_api_owner;

  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_shop%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_shop;
    PERFORM setup_create_role('robot_price_abtest');
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_customer%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_customer;
    GRANT zalando_backend_role TO zalando_zomcat_shop;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_admin%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_admin;
    GRANT zalando_backend_role TO zalando_zomcat_cct;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_cms%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_cms;
    GRANT zalando_backend_role TO zalando_zomcat_shop;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_export%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_export;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_user%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_user;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_addr%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_bm;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_partner%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_partner;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_catalog%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_catalog;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_acs%']) THEN
    GRANT zalando_backend_role TO zalando_zomcat_acs;
  END IF;
  END; $$;

  DO $$ BEGIN
  IF current_database() LIKE ANY(ARRAY['local_%','%_bm%']) THEN
    PERFORM setup_create_role('robot_coupon_create');
  END IF;
  END; $$;

  -- role with access to customer shard, needs to be assigned to invoker currently!
  SELECT setup_create_role('zalando_plproxy_customer_shard_usage');

  GRANT zalando_plproxy_customer_shard_usage TO zalando_backend_role;
  GRANT zalando_plproxy_customer_shard_usage TO zalando_api_owner; -- if security definer works this is necessary

  -- backwards bm_api compatibility , grants write permission to tables to zalando_hip
  SELECT setup_create_role('zalando_hip', ARRAY['zalando_data_writer']);

  SELECT setup_create_role('zalando_erp_executor');
  SELECT setup_create_role('zalando_erp', ARRAY['zalando_erp_executor']);

  SELECT setup_create_role('zalando_qa');
  SELECT setup_create_role('zalando_pm');

  SELECT setup_create_role('zalando_24x7' , ARRAY['zalando_data_reader']);

  -- otrs is really a login role, but we have to create it here to successfully run Integration Tests!
  SELECT setup_create_role('otrs');

  SELECT setup_create_role('zalando_commons_owner');
  SELECT setup_create_role('zalando_commons_usage');
  SELECT setup_create_role('zalando_commons_reader',ARRAY['zalando_commons_usage']);
  SELECT setup_create_role('zalando_commons_writer',ARRAY['zalando_commons_reader']);

  SELECT setup_create_role('zalando_commons_api_usage');
  SELECT setup_create_role('zalando_commons_api_owner' , ARRAY['zalando_commons_writer'] );
  SELECT setup_create_role('zalando_commons_api_executor', ARRAY['zalando_commons_api_usage']);

  SELECT setup_create_role('zalando_purchase_owner');
  SELECT setup_create_role('zalando_purchase_usage',ARRAY['zalando_commons_usage']);
  SELECT setup_create_role('zalando_purchase_reader',ARRAY['zalando_purchase_usage','zalando_commons_reader']);
  SELECT setup_create_role('zalando_purchase_writer',ARRAY['zalando_purchase_reader','zalando_commons_writer']);

  SELECT setup_create_role('zalando_purchase_pe_owner');
  SELECT setup_create_role('zalando_purchase_pe_usage',ARRAY['zalando_commons_usage']);
  SELECT setup_create_role('zalando_purchase_pe_reader',ARRAY['zalando_purchase_pe_usage','zalando_commons_reader']);
  SELECT setup_create_role('zalando_purchase_pe_writer',ARRAY['zalando_purchase_pe_reader']);

  SELECT setup_create_role('zalando_purchase_api_usage');
  SELECT setup_create_role('zalando_purchase_api_owner' , ARRAY['zalando_purchase_writer'] );
  SELECT setup_create_role('zalando_purchase_api_executor', ARRAY['zalando_purchase_api_usage','zalando_commons_api_executor'] );

  SELECT setup_create_role('zalando_processenginetools_owner');
  SELECT setup_create_role('zalando_processenginetools_usage',ARRAY['zalando_commons_usage']);
  SELECT setup_create_role('zalando_processenginetools_reader',ARRAY['zalando_processenginetools_usage','zalando_commons_reader']);
  SELECT setup_create_role('zalando_processenginetools_writer',ARRAY['zalando_processenginetools_reader','zalando_commons_writer']);

END;
