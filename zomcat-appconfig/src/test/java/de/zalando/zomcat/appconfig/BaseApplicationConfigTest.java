/**
 *
 */
package de.zalando.zomcat.appconfig;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.easymock.EasyMock;

import org.junit.Before;
import org.junit.Test;

import de.zalando.appconfig.Configuration;

import de.zalando.domain.Environment;

/**
 * @author  cvandrei
 */
public class BaseApplicationConfigTest extends BaseApplicationConfigImpl {

    Configuration confMock;

    @Before
    public void before() {
        resetMock();
    }

    /*
     * getEnvironment()
     ******************************************************************************************************************/

    @Test(expected = IllegalStateException.class)
    public void getEnvironmentNull() {

        // prepare
        expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(null);
        replay(this.confMock);

        // test
        getEnvironment();

    }

    @Test(expected = IllegalArgumentException.class)
    public void getEnvironmentInvalidEnvironment() {

        // prepare
        final String environmentConfigured = Environment.BE_STAGING.toString() + "aaa";

        try {
            Environment.valueOf(environmentConfigured);
            fail("should be an invalid environment: " + environmentConfigured);
        } catch (final IllegalArgumentException e) {
            // expected
        }

        expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(
            environmentConfigured);
        replay(this.confMock);

        // test
        getEnvironment();

    }

    @Test
    public void getEnvironmentValidEnvironment() {

        // prepare
        final Environment expected = Environment.BE_STAGING;
        final String environmentConfigured = expected.toString();

        expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(
            environmentConfigured);
        replay(this.confMock);

        // test
        final Environment env = getEnvironment();

        // verify
        assertEquals(expected, env);

        verify(this.confMock);

    }

    /*
     * isTesting()
     ******************************************************************************************************************/

    @Test(expected = IllegalStateException.class)
    public void isTestingNull() {

        // prepare
        expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(null);
        replay(this.confMock);

        // test
        isTesting();

    }

    @Test(expected = IllegalArgumentException.class)
    public void isTestingInvalidEnvironment() {

        // prepare
        final String environmentConfigured = Environment.BE_STAGING.toString() + "aaa";

        try {
            Environment.valueOf(environmentConfigured);
            fail("should be an invalid environment: " + environmentConfigured);
        } catch (final IllegalArgumentException e) {
            // expected
        }

        expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(
            environmentConfigured);
        replay(this.confMock);

        // test
        isTesting();

    }

    @Test
    public void isTestingNotTesting() {

        for (final Environment env : Environment.values()) {

            if (env.isLive()) {

                // prepare
                resetMock();
                expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(
                    env.toString());
                replay(this.confMock);

                // test
                final boolean testing = isTesting();

                // verify
                assertFalse(testing);

                verify(this.confMock);

            }

        }

    }

    @Test
    public void isTestingTesting() {

        for (final Environment env : Environment.values()) {

            if (!env.isLive()) {

                // prepare
                resetMock();
                expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(
                    env.toString());
                replay(this.confMock);

                // test
                final boolean testing = isTesting();

                // verify
                assertTrue(testing);

                verify(this.confMock);

            }

        }

    }

    /*
     * isLocalMachine()
     ******************************************************************************************************************/

    @Test(expected = IllegalStateException.class)
    public void isLocalMachineNull() {

        // prepare
        expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(null);
        replay(this.confMock);

        // test
        isLocalMachine();

    }

    @Test(expected = IllegalArgumentException.class)
    public void isLocalMachineInvalidEnvironment() {

        // prepare
        final String environmentConfigured = Environment.BE_STAGING.toString() + "aaa";

        try {
            Environment.valueOf(environmentConfigured);
            fail("should be an invalid environment: " + environmentConfigured);
        } catch (final IllegalArgumentException e) {
            // expected
        }

        expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(
            environmentConfigured);
        replay(this.confMock);

        // test
        isLocalMachine();

    }

    @Test
    public void isLocalMachineNotLocal() {

        for (final Environment env : Environment.values()) {

            if (!Environment.LOCAL.equals(env)) {

                // prepare
                resetMock();
                expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(
                    env.toString());
                replay(this.confMock);

                // test
                final boolean local = isLocalMachine();

                // verify
                assertFalse(local);

                verify(this.confMock);

            }

        }

    }

    @Test
    public void isLocalMachineLocal() {

        // prepare
        expect(this.confMock.getStringConfig(BaseApplicationConfigImpl.APPLICATION_ENVIRONMENT)).andReturn(
            Environment.LOCAL.toString());
        replay(this.confMock);

        // test
        final boolean local = isLocalMachine();

        // verify
        assertTrue(local);

        verify(this.confMock);

    }

    /*
     * test helpers
     ******************************************************************************************************************/

    private void resetMock() {
        this.confMock = EasyMock.createMock(Configuration.class);
        setConfig(confMock);
    }

}
