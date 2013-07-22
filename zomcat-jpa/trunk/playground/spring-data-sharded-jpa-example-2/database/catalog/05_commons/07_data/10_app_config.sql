-- feature configs
insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
  ('feature.queue.priceChange','on',true),
  ('feature.cache.effectivePriceDefinition.guava','on',true),
  ('feature.cache.effectivePriceDefinition.memcached','on',true),
  ('feature.deleteThreshold', 'on', true),
  ('feature.eventlog.priceDefinitionChange', 'on', true),
  ('feature.eventlog.priceChangePublish', 'on', true),
  ('feature.eventlog.priceChangeArchived', 'on', true),
  ('feature.priceImport.recursiveDelete', 'off', true),
  ('feature.priceImport.noLayouting', 'on', true),
  ('feature.priceImport.noFallbacking', 'on', true),
  ('feature.priceImport.riskPromotional', 'on', true),
  ('feature.articleSearch.updateSolr', 'on', true),
  ('feature.articleSearch.export.keepFile', 'on', true),
  ('feature.articleBulkUpdate.verboseReport', 'on', true);


-- features config: base fallback price
insert into zcat_commons.app_config (ac_key, ac_appdomain_id, ac_value, ac_is_online_updateable) values
  ('feature.materialization.lenientPriceValidation', null, 'off', true),
  ('feature.baseFallbackPrice.create', null, 'off', true),
  ('feature.baseFallbackPrice.events', null, 'off', true),
  ('feature.baseFallbackPrice.materialization', 1, 'off', true),
  ('feature.baseFallbackPrice.materialization', 2, 'off', true),
  ('feature.baseFallbackPrice.materialization', 3, 'off', true),
  ('feature.baseFallbackPrice.materialization', 4, 'off', true),
  ('feature.baseFallbackPrice.materialization', 5, 'off', true),
  ('feature.baseFallbackPrice.materialization', 6, 'off', true),
  ('feature.baseFallbackPrice.materialization', 7, 'off', true),
  ('feature.baseFallbackPrice.materialization', 8, 'off', true),
  ('feature.baseFallbackPrice.materialization', 9, 'off', true),
  ('feature.baseFallbackPrice.materialization', 10, 'off', true),
  ('feature.baseFallbackPrice.materialization', 11, 'off', true),
  ('feature.baseFallbackPrice.materialization', 12, 'off', true),
  ('feature.baseFallbackPrice.materialization', 13, 'off', true),
  ('feature.baseFallbackPrice.materialization', 14, 'off', true),
  ('feature.baseFallbackPrice.materialization', 15, 'off', true),
  ('feature.baseFallbackPrice.materialization', 16, 'off', true),
  ('feature.baseFallbackPrice.materialization', 17, 'off', true),
  ('feature.baseFallbackPrice.materialization', 18, 'off', true),
  ('feature.baseFallbackPrice.materialization', 19, 'off', true),
  ('feature.baseFallbackPrice.materialization', 20, 'off', true),
  ('feature.baseFallbackPrice.materialization', 21, 'off', true),
  ('feature.baseFallbackPrice.materialization', 22, 'off', true),
  ('feature.baseFallbackPrice.materialization', 23, 'off', true),
  ('feature.baseFallbackPrice.materialization', 24, 'off', true),
  ('feature.baseFallbackPrice.materialization', 25, 'off', true),
  ('feature.baseFallbackPrice.materialization', 26, 'off', true),
  ('feature.baseFallbackPrice.materialization', 27, 'off', true),
  ('feature.baseFallbackPrice.materialization', 28, 'off', true),
  ('feature.baseFallbackPrice.materialization', 29, 'off', true),
  ('feature.baseFallbackPrice.materialization', 30, 'off', true),
  ('feature.baseFallbackPrice.materialization', 31, 'off', true),
  ('feature.baseFallbackPrice.materialization', 32, 'off', true),
  ('feature.baseFallbackPrice.materialization', 33, 'off', true),
  ('feature.baseFallbackPrice.materialization', 34, 'off', true),
  ('feature.baseFallbackPrice.materialization', 35, 'off', true),
  ('feature.baseFallbackPrice.materialization', 36, 'off', true),
  ('feature.baseFallbackPrice.materialization', 37, 'off', true),
  ('feature.baseFallbackPrice.materialization', 101, 'off', true);

-- job configs
insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
  ('jobGroupConfig.exportJobs.active','true',true),
  ('jobConfig.priceChangeExportJob.limit','300000',true), -- 5 minutes in milliseconds
  ('jobConfig.priceChangeExportJob.active','true',true),
  ('jobConfig.priceChangeExportJob.messageTTL','3600000',true), -- 60 minutes in milliseconds
  ('jobConfig.priceChangeExportJob.threadCount','8',true),
  ('jobConfig.priceArchiveExportJob.limit','10000',true), -- chunk to process limit
  ('jobConfig.priceArchiveExportJob.active','true',true),
  ('jobConfig.priceArchiveExportJob.threadCount','8',true),
  ('jobConfig.maintenanceJob.appInstanceKey', '*', true),
  ('jobConfig.maintenanceJob.limit','10000',true), -- chunk to process limit
  ('jobConfig.maintenanceJob.active','true',true),
  ('jobConfig.priceImportJob.limit','1',true),
  ('jobConfig.priceImportJob.active','true',true),
  ('jobConfig.priceImportJob.threadCount','1',true),
  ('jobConfig.articleSearchUpdateJob.limit','10000',true), -- chunk to process limit
  ('jobConfig.articleSearchUpdateJob.active','true',true),
  ('jobConfig.articleSearchUpdateJob.threadCount','8',true),
  ('jobConfig.articleSearchUpdateJob.appInstanceKey', '*', true);

  -- other configs
insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
  ('priceImport.max.csv.lineCount', '100000', true),
  ('priceArchiveService.bulkSize', '200', true);

do $SQL$

begin

    -- LOCAL
    if current_database() like 'local_%' then
      insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
      ('jobConfig.priceChangeExportJob.appInstanceKey', '*', true),
      ('jobConfig.priceArchiveExportJob.appInstanceKey', 'local_local', true),
      ('jobConfig.priceImportJob.appInstanceKey','local_local',true),
      ('priceChangePublisherService.enabled', 'true', true),
      ('test.priceChangePublisherService.enabled', 'true', true),--test
      ('test.priceChangePublisherService.enabled2', 'true', true),--test
      ('priceImport.processing.waitTime', '60', true),
      ('priceImport.riskPromotional.allowedUsers', 'test@zalando.de', true),
      ('priceImport.riskPromotional.allowedPLRCs', '1011100,1012100', true);
    end if;

    -- DEVELOPMENT
    if current_database() like 'development_%' then
      insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
      ('priceImport.changeLog.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsv.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsvBackup.dir', '/data/zalando/storage/catalog/price-import', true),
      ('priceImport.deleteThreshold', '500', true),
      ('jobConfig.priceChangeExportJob.appInstanceKey', '*', true),
      ('jobConfig.priceArchiveExportJob.appInstanceKey', 'z-01_p7031', true),
      ('jobConfig.priceImportJob.appInstanceKey','z-01_p7031',true),
      ('priceChangePublisherService.enabled', 'true', true),
      ('priceImport.noLayouting.allowedUsers', 'praveen.kumar.bhadrapur@zalando.de, melanie.pries@zalando.de, melanie.domula@zalando.de, brian.gaulden@zalando.de, david.koenig@zalando.de, artur.weber@zalando.de, matthias.heurich@zalando.de, katharina.luxat@zalando.de', true),
      ('priceImport.noFallbacking.allowedUsers', 'praveen.kumar.bhadrapur@zalando.de, melanie.pries@zalando.de, melanie.domula@zalando.de, brian.gaulden@zalando.de, david.koenig@zalando.de, artur.weber@zalando.de, matthias.heurich@zalando.de, katharina.luxat@zalando.de, marie.kastirr@zalando-lounge.de, lucienne.dziekanski@zalando-lounge.de', true),
      ('priceImport.recursiveDelete.allowedUsers', 'torsten.reichenbach@zalando.de,andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,martin.benke@zalando.de,christina.grosse@zalando.de', true),
      ('priceImport.riskPromotional.allowedUsers', 'test@zalando.de,andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,torsten.kunz@zalando.de,mareike.neumann@zalando.de,lars.karbe@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,tamas.eppel@zalando.de,john.jochens@zalando.de,anton.smolich@zalando.de', true),
      ('priceImport.riskPromotional.allowedPLRCs', '1011100,1012100', true);
    end if;

    -- INTEGRATION
    if current_database() like 'integration_%' then
      insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
      ('priceImport.changeLog.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsv.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsvBackup.dir', '/data/zalando/storage/catalog/price-import', true),
      ('priceImport.deleteThreshold', '500', true),
      ('priceImport.processing.waitTime', '60', true),
      ('jobConfig.priceChangeExportJob.appInstanceKey', '*', true),
      ('jobConfig.priceArchiveExportJob.appInstanceKey', 'z-01_p7001', true),
      ('jobConfig.priceImportJob.appInstanceKey','z-01_p7001',true),
      ('priceChangePublisherService.enabled', 'true', true),
      ('priceImport.noLayouting.allowedUsers', 'praveen.kumar.bhadrapur@zalando.de, melanie.pries@zalando.de, melanie.domula@zalando.de, brian.gaulden@zalando.de, david.koenig@zalando.de, artur.weber@zalando.de, matthias.heurich@zalando.de, katharina.luxat@zalando.de', true),
      ('priceImport.noFallbacking.allowedUsers', 'praveen.kumar.bhadrapur@zalando.de, melanie.pries@zalando.de, melanie.domula@zalando.de, brian.gaulden@zalando.de, david.koenig@zalando.de, artur.weber@zalando.de, matthias.heurich@zalando.de, katharina.luxat@zalando.de, marie.kastirr@zalando-lounge.de, lucienne.dziekanski@zalando-lounge.de', true),
      ('priceImport.recursiveDelete.allowedUsers', 'torsten.reichenbach@zalando.de,andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,martin.benke@zalando.de,christina.grosse@zalando.de', true),
      ('priceImport.riskPromotional.allowedUsers', 'test@zalando.de,andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,torsten.kunz@zalando.de,mareike.neumann@zalando.de,lars.karbe@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,tamas.eppel@zalando.de,john.jochens@zalando.de,anton.smolich@zalando.de', true),
      ('priceImport.riskPromotional.allowedPLRCs', '1011100,1012100', true);
    end if;

    -- LIVE
    if current_database() like 'prod_%' then
      insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
      ('priceImport.changeLog.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsv.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsvBackup.dir', '/data/zalando/storage/catalog/price-import', true),
      ('priceImport.deleteThreshold', '500', true),
      ('priceImport.processing.waitTime', '60', true),
      ('jobConfig.priceChangeExportJob.appInstanceKey', '*', true),
      ('jobConfig.priceArchiveExportJob.appInstanceKey', 'be01_p7020', true),
      ('jobConfig.priceImportJob.appInstanceKey','be01_p7020',true),
      ('priceChangePublisherService.enabled', 'false', true),
      ('priceImport.noLayouting.allowedUsers', 'melanie.pries@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,katharina.luxat@zalando.de', true),
      ('priceImport.noFallbacking.allowedUsers', 'melanie.pries@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,katharina.luxat@zalando.de,katharina.lokwenz@zalando-lounge.de,robert.plewa@zalando-lounge.de', true),
      ('priceImport.recursiveDelete.allowedUsers', 'melanie.pries@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de', true),
      ('priceImport.riskPromotional.allowedUsers', 'melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,torsten.kunz@zalando.de,mareike.neumann@zalando.de,lars.karbe@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de', true),
      ('priceImport.riskPromotional.allowedPLRCs', '1011100,1012100', true);
    end if;

    -- PATCH_STAGING
    if current_database() like 'patch_%' then
      insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
      ('priceImport.changeLog.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsv.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsvBackup.dir', '/data/zalando/storage/catalog/price-import', true),
      ('priceImport.deleteThreshold', '500', true),
      ('priceImport.processing.waitTime', '60', true),
      ('jobConfig.priceChangeExportJob.appInstanceKey', '*', true),
      ('jobConfig.priceArchiveExportJob.appInstanceKey', 'te2a_p7001', true),
      ('jobConfig.priceImportJob.appInstanceKey','te2a_p7001',true),
      ('priceChangePublisherService.enabled', 'true', true),
      ('priceImport.noLayouting.allowedUsers', 'melanie.pries@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,katharina.luxat@zalando.de', true),
      ('priceImport.noFallbacking.allowedUsers', 'melanie.pries@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,katharina.luxat@zalando.de, marie.kastirr@zalando-lounge.de,lucienne.dziekanski@zalando-lounge.de', true),
      ('priceImport.recursiveDelete.allowedUsers', 'andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,martin.benke@zalando.de,christina.grosse@zalando.de', true),
      ('priceImport.riskPromotional.allowedUsers', 'test@zalando.de,andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,torsten.kunz@zalando.de,mareike.neumann@zalando.de,lars.karbe@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,tamas.eppel@zalando.de,john.jochens@zalando.de,anton.smolich@zalando.de', true),
      ('priceImport.riskPromotional.allowedPLRCs', '1011100,1012100', true);
    end if;

    -- PERFORMANCE_STAGING (uncomment when there will be database for this)
    /*
    if current_database() like 'release_%' then
      insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
      ('jobConfig.priceChangeExportJob.appInstanceKey', 'xxx', true),
      ('jobConfig.priceArchiveExportJob.appInstanceKey', 'xxx', true),
      ('priceChangePublisherService.enabled', 'true', true);
    end if;
    */

    -- RELEASE_STAGING
    if current_database() like 'release_%' then
      insert into zcat_commons.app_config (ac_key, ac_value, ac_is_online_updateable) values
      ('priceImport.changeLog.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsv.dir', '/data/zalando/storage/catalog/price-import', true),
      ('jobConfig.priceImportCsvBackup.dir', '/data/zalando/storage/catalog/price-import', true),
      ('priceImport.deleteThreshold', '500', true),
      ('priceImport.processing.waitTime', '60', true),
      ('jobConfig.priceChangeExportJob.appInstanceKey', '*', true),
      ('jobConfig.priceArchiveExportJob.appInstanceKey', 'te1a_p7001', true),
      ('jobConfig.priceImportJob.appInstanceKey','te1a_p7001',true),
      ('priceChangePublisherService.enabled', 'true', true),
      ('priceImport.noLayouting.allowedUsers', 'andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,artur.weber@zalando.de,katharina.luxat@zalando.de,matthias.heurich@zalando.de,martin.benke@zalando.de,christina.grosse@zalando.de', true),
      ('priceImport.noFallbacking.allowedUsers', 'andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,artur.weber@zalando.de,katharina.luxat@zalando.de,matthias.heurich@zalando.de,martin.benke@zalando.de,christina.grosse@zalando.de', true),
      ('priceImport.recursiveDelete.allowedUsers', 'andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,martin.benke@zalando.de,christina.grosse@zalando.de', true),
      ('priceImport.riskPromotional.allowedUsers', 'test@zalando.de,andre.heisel@zalando.de,rene.huefner@zalando.de,tom.fricke@zalando.de,melanie.domula@zalando.de,brian.gaulden@zalando.de,david.koenig@zalando.de,torsten.kunz@zalando.de,mareike.neumann@zalando.de,lars.karbe@zalando.de,artur.weber@zalando.de,matthias.heurich@zalando.de,tamas.eppel@zalando.de,john.jochens@zalando.de,anton.smolich@zalando.de', true),
      ('priceImport.riskPromotional.allowedPLRCs', '1011100,1012100', true);
    end if;

end;

$SQL$;
