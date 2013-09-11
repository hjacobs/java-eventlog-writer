create table zcat_commons.price_fallback_appdomains (
    pfa_appdomain_id        smallint   unique references zz_commons.appdomain (ad_id),
    pfa_fallback_sequence   smallint[]
);

/*
  this data is disabled and should be created dynamically within integration tests that depends on it
  TODO: create a DefaultPriceFallBackAppDomainsGenerator class that is generating this data for each integration test

  2012-12-17:
  Added  IT, UK, SE
         15, 16, 27
  to fallback lists for ZEOS-11542
  */
insert into zcat_commons.price_fallback_appdomains (pfa_appdomain_id, pfa_fallback_sequence)
values
(1,   ARRAY [19,  11,  5,   25,       15,  16,  27]),
(5,   ARRAY [25,  19,  1,   11,       15,  16,  27]),
(11,  ARRAY [1,   19,  25,  5,        15,  16,  27]),
(15,  ARRAY [25,  1,   5,   11,            16,  27]),
(16,  ARRAY [24,  1,   11,  19,            15,  27]),
(17,  ARRAY []::smallint[]),
(18,  ARRAY []::smallint[]),
(19,  ARRAY [1,   11,  5,   25,       15,  16,  27]),
(20,  ARRAY [1,   25,  19,  24,       15,  16,  27]),
(21,  ARRAY [20,  1,   25,  19,  24,  15,  16,  27]),
(22,  ARRAY []::smallint[]),
(24,  ARRAY [1,   11,  16,  19,            15,  27]),
(25,  ARRAY [5,   11,  15,  1,             16,  27]),
(26,  ARRAY [25,  5,   11,  15,  1,        16,  27]),
(27,  ARRAY [29,  32,  25,  1,             15,  16]),
(28,  ARRAY [32,  24,  11,  1,        15,  16,  27]),
(29,  ARRAY [1,   5,   25,  32,       15,  16,  27]),
(30,  ARRAY [5,   25,  11,  1,        15,  16,  27]),
(32,  ARRAY [29,  28,  27,  1,        15,  16,  27]),
(35,  ARRAY []::smallint[]),
(36,  ARRAY []::smallint[]),
(37,  ARRAY [36]),
(38,  ARRAY[]::smallint[]),
(101, ARRAY [1,  19,  11,  5,   25,   15,  16,  27]),
(201, ARRAY [1,  19,  11,  5,   25,   15,  16,  27]);
