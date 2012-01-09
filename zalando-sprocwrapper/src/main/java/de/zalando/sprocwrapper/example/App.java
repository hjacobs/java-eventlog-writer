package de.zalando.sprocwrapper.example;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.DataSource;

import com.jolbox.bonecp.BoneCPDataSource;

import de.zalando.sprocwrapper.dsprovider.ArrayDataSourceProvider;

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

        BoneCPDataSource ds2 = new BoneCPDataSource();          // create a new datasource object
        ds2.setJdbcUrl("jdbc:postgresql://localhost/postgres"); // set the JDBC url
        ds2.setUsername("postgres");                            // set the username
        ds2.setPassword("postgres");                            // set the password

        ExampleSProcServiceImpl service = new ExampleSProcServiceImpl(new ArrayDataSourceProvider(
                    new DataSource[] {ds, ds2}));

        System.out.println(service.getSimpleInt());

        service.getSimpleIntIgnore();

        /****
         CREATE OR REPLACE FUNCTION create_article_simple_items(sku text, stockid integer, quantity integer, price integer,
         referencenumber text)
         RETURNS text AS
         $BODY$ begin return sku || ' ' || stockid || ' ' || quantity || ' ' || price || ' ' || referencenumber; end; $BODY$
         LANGUAGE plpgsql VOLATILE
         COST 100;

          CREATE OR REPLACE FUNCTION get_simple_int()
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
        for (ExampleResult r : service.getResult()) {
            System.out.println("a: " + r.a + " b: " + r.b);
        }

        // Query a single TestResult Object
        ExampleResult r = service.getSingleResult();
        System.out.println("Single result: a) " + r.a + " b) " + r.b);

        System.out.println(service.getBla());

        System.out.println(service.getDatabase(0));
        System.out.println(service.getDatabase(1));
    }
}
