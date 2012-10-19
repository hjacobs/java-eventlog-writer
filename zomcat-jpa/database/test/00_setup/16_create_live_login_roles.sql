DO $$
BEGIN

  IF current_database() LIKE ANY(ARRAY['prod_zalos%','tws_zal_db']) THEN
        -- in zalos database we do not now really need any of our million login roles
        PERFORM setup_create_role('robot_zalos_data_writer', ARRAY['zalando_data_writer'], password := 'K7AraWlYemzES83HZXM3LHtoRomkSb');
        -- ALTER ROLE robot_zalos_data_writer SET search_path=zalos, zalosdia, zalosdhl, zalando_data, public;
  END IF;

  IF current_database() LIKE ANY(ARRAY['%prod%','zalando_shop_db','zalando_export_db']) THEN

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_customer'],'K7AraWlYemzES83HZXM3LHtoRomkSb')
          FROM (
                VALUES('zomcat_p9620'),
                      ('zomcat_p9621'),
                      ('zomcat_p9622')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_shop'],'q00N2UnT7ImFRGZgxP2KLrMsAIti3l')
          FROM (
                VALUES('zomcat_p0120'),
                      ('zomcat_p0121'),
                      ('zomcat_p0122'),
                      ('zomcat_p0123'),
                      ('zomcat_p0110')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_bm'],'UJNPk3yX7LHLysl1djNf7M2Oy6cZeJ')
          FROM (
                VALUES('zomcat_p9820'),
                      ('zomcat_p9821'),
                      ('zomcat_p9822')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_cms'],'OqJLayxaNw3iIe2ZkNdq6CXE92SofT')
          FROM (
                VALUES('zomcat_p9320')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_user'],'y6RWQAo49edewYspRnSFUgfqgvaSba')
          FROM (
                VALUES('zomcat_p9520')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_admin'],'cNq2PS3o5ZupktSYPejuViZWCjaxwc')
          FROM (
                VALUES('zomcat_p9920')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_cct'],'WynyL8h0lpgtgqlD1o871orndVdU9W')
          FROM (
                VALUES('zomcat_p9420')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_export'],'BErZI1eoeK6XLoCGhBJSiykrH9AcSn')
          FROM (
                VALUES('zomcat_p8620')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_partner'],'BErZI1eoeK6XLoCGhBJSiykrH9AcSn')
          FROM (
                VALUES('zomcat_p7520')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_catalog'],'md5cad8023ee53311ceecf4b2567e86b6c0')
          FROM (
                VALUES('zomcat_p7020')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_acs'],'md50e4420903aa98ac79bdfccbe2fe57c3c')
          FROM (
                VALUES('zomcat_p6520')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_stock'],'md58bd38ba7818ccead37ff198d38dbd8f9')
          FROM (
                VALUES('zomcat_p6420')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_logistics'],'md5f4d98d1e3cdee5fb20192f4af9cb3fde')
          FROM (
                VALUES('zomcat_p6020')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_payment'],'md51e24746cf0ad28d822427d6de2c0b4f2')
          FROM (
                VALUES('zomcat_p6320')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_orderengine'],'md5b3a8d15e7a59e851d09d34e88dca7876')
          FROM (
                VALUES('zomcat_p6220')
                ) t (rname);

  END IF;

  IF current_database() LIKE ANY(ARRAY['%best%','%fest%','%staging%','%testing%','%integration%','local_%']) THEN

        PERFORM setup_create_role('robot_zalos_data_writer', ARRAY['zalando_data_writer'], password := 'robot_zalos_data_writer');

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_customer'],rname)
          FROM (
                VALUES('zomcat_p9600')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_shop'],rname)
          FROM (
                VALUES('zomcat_p0100')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_bm'],rname)
          FROM (
                VALUES('zomcat_p9800')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_cms'],rname)
          FROM (
                VALUES('zomcat_p9300')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_user'],rname)
          FROM (
                VALUES('zomcat_p9500')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_admin'],rname)
          FROM (
                VALUES('zomcat_p9900')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_cct'],rname)
          FROM (
                VALUES('zomcat_p9400')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_export'],rname)
          FROM (
                VALUES('zomcat_p8600')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_partner'],rname)
          FROM (
                VALUES('zomcat_p7500')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_wh'],rname)
          FROM (
                VALUES('zomcat_p5200'),('zomcat_p4800'),('zomcat_p5320')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_purchase'],rname)
          FROM (
                VALUES('zomcat_p5100')
                ) t (rname);

        PERFORM setup_create_role(rname,ARRAY['zalando_zomcat_pricecrawler'] ,rname)
          FROM (
                VALUES('zomcat_p4100')
                ) t (rname);

  END IF;

END;
$$;
