CREATE OR REPLACE FUNCTION zcat_commons.create_optimistic_locking_version_trigger(table_name regclass)
  RETURNS void AS
$BODY$
/*
This function creates a trigger on the version column of the given table, if there
not yet another trigger function exists. Name of trigger is build based on convention
'trigger_tablename_ol_version'
Since it may be used in deploy process, it will raise an exception in any error case.

select zcat_commons.create_optimistic_locking_version_trigger('schema.table'::regclass);

$Id$
$HeadURL$
*/
DECLARE
  l_colname text;
  l_funcname text;
  l_functext text;
  l_table_name text;
  l_schema_name text;
  l_type text;
  l_trigger text;
  l_drop_trigger text;
  l_assignment text;
BEGIN
  IF table_name IS NULL THEN
    RAISE EXCEPTION 'Tablename is required';
  END IF;

  SELECT ns.nspname, cl.relname
    INTO l_schema_name, l_table_name
    FROM pg_catalog.pg_class cl
    JOIN pg_namespace ns on cl.relnamespace = ns.oid
   WHERE cl.oid = table_name::oid;

  IF length(l_table_name) <> length(quote_ident(l_table_name))
     OR length(l_schema_name) <> length(quote_ident(l_schema_name)) THEN
    RAISE EXCEPTION 'Quoted Identifier are currently not supported';
  END IF;

  SELECT attname, typname INTO l_colname, l_type
    FROM pg_attribute
    JOIN pg_type pt on atttypid = pt.oid
   WHERE attrelid = table_name::oid
     AND attname LIKE '%_version'
     AND attisdropped = false
     AND attnum > 0 -- is regular column
     AND (typname = 'int4');

  IF NOT FOUND OR l_colname IS NULL THEN
    RAISE EXCEPTION  'No version column found';
  END IF;

  l_funcname := 'trigger_' ||l_table_name|| '_ol_version';

  l_functext := $$CREATE OR REPLACE FUNCTION $$|| l_schema_name ||$$.$$ ||l_funcname||$$()
  RETURNS trigger AS
  $GENFUNC$
  BEGIN
    IF (TG_OP = 'UPDATE') THEN
      IF NEW.$$||l_colname||$$ IS NOT NULL AND NEW.$$||l_colname||$$ IS DISTINCT FROM OLD.$$||l_colname||$$ THEN
        -- the value should be the same.
        -- throw an optimistic locking exception
        RAISE EXCEPTION  'optimistic lock exception found. version does not match old [%] != new [%]',
                          OLD.$$||l_colname||$$,
                          NEW.$$||l_colname||$$
        USING ERRCODE = 'Z0002'; -- error code = optimistic locking
      ELSE
        -- increase the value by 1
        NEW.$$||l_colname||$$ := OLD.$$||l_colname||$$ + 1;
      END IF;
      RETURN NEW;
    ELSIF (TG_OP = 'DELETE') THEN
      RETURN OLD;
    ELSIF (TG_OP = 'INSERT') THEN
      NEW.$$||l_colname||$$ := 0;
      RETURN NEW;
    END IF;
  END
  $GENFUNC$
  language 'plpgsql'$$;

  l_trigger := $$CREATE TRIGGER $$ || l_funcname||$$
    BEFORE INSERT OR UPDATE ON $$||l_schema_name||'.'||l_table_name||$$
    FOR EACH ROW
    execute procedure $$||l_schema_name||'.'||l_funcname||'()';

  l_drop_trigger := $$DROP TRIGGER IF EXISTS  $$ || l_funcname||$$ ON $$||l_schema_name||'.'||l_table_name;

  EXECUTE l_functext;

  EXECUTE l_drop_trigger;
  EXECUTE l_trigger;

END;
$BODY$
language 'plpgsql';

COMMENT ON FUNCTION zcat_commons.create_optimistic_locking_version_trigger( regclass) IS
$$This function creates a trigger on the version column of the given table, if there
not yet another trigger function exists. Name of trigger is build based on convention
'trigger_tablename_ol_version'
Since it may be used in deploy process, it will raise an exception in any error case.$$
