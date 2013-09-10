package de.zalando.jpa.config;

/**
 * Defines Refactoring-/Typesafe Profiles to test.
 *
 * @author  jbellmann
 */
public interface TestProfiles {

    String HSQL = "HSQL";
    String H2 = "H2";
    String POSTGRES = "POSTGRES";
    String H2_SHARDED = "H2_SHARDED";
    String H2_SHARDED_4 = "H2_SHARDED_4";

}
