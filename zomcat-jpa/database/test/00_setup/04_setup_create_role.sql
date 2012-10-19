CREATE OR REPLACE FUNCTION setup_create_role(role_name text , inroles text[] default null , password text default null ) RETURNS text AS $$
    DECLARE
      r record;
      l_roles text;
    BEGIN
          BEGIN
                  -- check if role exists
                  EXECUTE 'CREATE ROLE ' || quote_ident(role_name) || COALESCE(' WITH LOGIN PASSWORD ' || quote_literal(password),'') || ' ;';

                  RAISE INFO 'Role % created',  quote_ident(role_name);
          EXCEPTION WHEN OTHERS THEN
                  RAISE INFO 'ROLE % already exists / or other error', quote_ident(role_name) USING ERRCODE = SQLSTATE, DETAIL = SQLERRM;
          END;

      SELECT string_agg ( quote_ident ( role ) ,','  ) INTO l_roles FROM unnest ( inroles ) AS t ( role );

          IF length(l_roles) > 0 THEN
        EXECUTE 'GRANT ' || l_roles || ' TO ' || quote_ident(role_name) || ';';
        RAISE INFO 'Granted roles % to %', l_roles , quote_ident(role_name);
          END IF;

      RETURN 'role created ' || quote_ident(role_name);
    END;
  $$ LANGUAGE plpgsql SECURITY INVOKER;
