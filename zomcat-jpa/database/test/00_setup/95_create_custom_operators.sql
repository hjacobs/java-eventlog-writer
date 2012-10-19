BEGIN;

SET search_path = public;
SET ROLE to postgres;

-- int2 operators
CREATE OR REPLACE FUNCTION int2_bitwise_contained(int2, int2)
  RETURNS boolean AS
'SELECT int2eq( int2and( $1, $2 ), $1 )'
  LANGUAGE SQL IMMUTABLE STRICT
  COST 1;
COMMENT ON FUNCTION int2_bitwise_contained(int2, int2) IS 'bitwise contained in';

CREATE OR REPLACE FUNCTION int2_bitwise_contains(int2, int2)
  RETURNS boolean AS
'SELECT int2eq( int2and( $1, $2 ), $2 )'
  LANGUAGE SQL IMMUTABLE STRICT
  COST 1;
COMMENT ON FUNCTION int2_bitwise_contains(int2, int2) IS 'bitwise contains';

CREATE OR REPLACE FUNCTION int4_bitwise_contained(int4, int4)
  RETURNS boolean AS
'SELECT int4eq( int4and( $1, $2 ), $1 )'
  LANGUAGE SQL IMMUTABLE STRICT
  COST 1;
COMMENT ON FUNCTION int4_bitwise_contained(int4, int4) IS 'bitwise contained in';

CREATE OR REPLACE FUNCTION int4_bitwise_contains(int4, int4)
  RETURNS boolean AS
'SELECT int4eq( int4and( $1, $2 ), $2 )'
  LANGUAGE SQL IMMUTABLE STRICT
  COST 1;
COMMENT ON FUNCTION int4_bitwise_contains(int4, int4) IS 'bitwise contains';

-- int8 operators
CREATE OR REPLACE FUNCTION int8_bitwise_contained(int8, int8)
  RETURNS boolean AS
'SELECT int8eq( int8and( $1, $2 ), $1 )'
  LANGUAGE SQL IMMUTABLE STRICT
  COST 1;
COMMENT ON FUNCTION int8_bitwise_contained(int8, int8) IS 'bitwise contained in';

CREATE OR REPLACE FUNCTION int8_bitwise_contains(int8, int8)
  RETURNS boolean AS
'SELECT int8eq( int8and( $1, $2 ), $2 )'
  LANGUAGE SQL IMMUTABLE STRICT
  COST 1;
COMMENT ON FUNCTION int8_bitwise_contains(int8, int8) IS 'bitwise contains';

DO $SQL$

BEGIN

CREATE OPERATOR <@(
  PROCEDURE = int2_bitwise_contained,
  LEFTARG = int2,
  RIGHTARG = int2,
  COMMUTATOR = '@>',
  RESTRICT = contsel,
  JOIN = contjoinsel);

CREATE OPERATOR @>(
  PROCEDURE = int2_bitwise_contains,
  LEFTARG = int2,
  RIGHTARG = int2,
  COMMUTATOR = '<@',
  RESTRICT = contsel,
  JOIN = contjoinsel);

CREATE OPERATOR <@(
  PROCEDURE = int4_bitwise_contained,
  LEFTARG = int4,
  RIGHTARG = int4,
  COMMUTATOR = '@>',
  RESTRICT = contsel,
  JOIN = contjoinsel);

CREATE OPERATOR @>(
  PROCEDURE = int4_bitwise_contains,
  LEFTARG = int4,
  RIGHTARG = int4,
  COMMUTATOR = '<@',
  RESTRICT = contsel,
  JOIN = contjoinsel);

CREATE OPERATOR <@(
  PROCEDURE = int8_bitwise_contained,
  LEFTARG = int8,
  RIGHTARG = int8,
  COMMUTATOR = '@>',
  RESTRICT = contsel,
  JOIN = contjoinsel);

CREATE OPERATOR @>(
  PROCEDURE = int8_bitwise_contains,
  LEFTARG = int8,
  RIGHTARG = int8,
  COMMUTATOR = '<@',
  RESTRICT = contsel,
  JOIN = contjoinsel);

EXCEPTION WHEN OTHERS THEN
  RAISE INFO 'Could not create bitwise set operators for int2,int4 and int8 probably because they already exist in the given database' USING ERRCODE = SQLSTATE, DETAIL = SQLERRM;
END; $SQL$;

GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO public;

-- int2 tests
SELECT 1::smallint <@ 3::smallint, 3::smallint @> 1::smallint;
-- int4 tests
SELECT 4 <@ 5, 3 @> 1;
-- int8 tests
SELECT 4::int8 <@ 5::int8, 3::int8 @> 1::int8;


RESET search_path;
RESET ROLE;

COMMIT;
