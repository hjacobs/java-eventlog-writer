package de.zalando.jpa.another.project;

import org.eclipse.persistence.config.SessionCustomizer;

import org.junit.Assert;
import org.junit.Test;

/**
 * Should ensure everything is visible in other packages.
 *
 * @author  jbellmann
 */
public class ProjectTest {

    @Test
    public void customImplementationOnDifferentPackage() {
        SessionCustomizer sc = new ProjectSessionCustomizer();
        Assert.assertNotNull(sc);
    }
}
