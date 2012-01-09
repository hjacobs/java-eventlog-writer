package de.zalando.storedprocedurewrapper;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.jolbox.bonecp.BoneCPDataSource;

/**
 * Hello world!
 */
public class App {

    public static void main(final String[] args) {

        try {
            Class.forName("org.postgresql.Driver"); // load the DB driver
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }

        BoneCPDataSource ds = new BoneCPDataSource();              // create a new datasource object
        ds.setJdbcUrl("jdbc:postgresql://localhost/zalando_test"); // set the JDBC url
        ds.setUsername("postgres");                                // set the username
        ds.setPassword("postgres");                                // set the password

        StockService service = new StockService(new StockServiceDataSourceProvider(ds));

        System.out.println(service.getSimpleInt());

        /****
         CREATE OR REPLACE FUNCTION create_article_simple_items(sku text, stockid integer, quantity integer, price integer,
         referencenumber text)
         RETURNS text AS
         $BODY$ begin return sku || ' ' || stockid || ' ' || quantity || ' ' || price || ' ' || referencenumber; end; $BODY$
         LANGUAGE plpgsql VOLATILE
         COST 100;

          CREATE OR REPLACE FUNCTION getsimpleint()
          RETURNS integer AS
        ' begin return 3; end; '
          LANGUAGE plpgsql VOLATILE
          COST 100;
         **/

        // Query an sproc returning a single String ( sproc concatenates input parameters to one string )
        System.out.println(service.createArticleSimpleItems("sku", 1, 12, 13, "1001"));

        System.out.println(service.getOtherInt());

        // Query for a Single Integer Value
        System.out.println(service.getSelectValue(1234));

        // Query for a Multi Row Resultset of TestResult Objects
        for (TestResult r : service.getResult()) {
            System.out.println("a: " + r.a + " b: " + r.b);
        }

        // Query a single TestResult Object
        TestResult r = service.getSingleResult();
        System.out.println("Single result: a) " + r.a + " b) " + r.b);

        System.out.println(service.getBla());
    }
}
