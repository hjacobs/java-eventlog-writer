package de.zalando.data.jpa.domain.support;

import static org.mockito.Mockito.mock;

import javax.sql.DataSource;

import org.junit.Test;

import de.zalando.data.jpa.domain.support.jdbc.StoredProcedureBusinessKeyGenerator;

/**
 * Ensures not null DataSource in constructor.
 *
 * @author  jbellmann
 */
public class StoredProcedureBusinessKeyGeneratorTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNullDataSourceRaisesException() {
        new StoredProcedureBusinessKeyGenerator(null);
    }

    @Test
    public void testNotNullDataSource() {
        new StoredProcedureBusinessKeyGenerator(mock(DataSource.class));
    }

}
