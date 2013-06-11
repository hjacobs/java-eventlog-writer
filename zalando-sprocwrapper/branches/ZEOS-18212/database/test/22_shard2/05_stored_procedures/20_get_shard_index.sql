CREATE OR REPLACE FUNCTION get_shard_index()
          RETURNS integer AS
        ' begin return 1; end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
