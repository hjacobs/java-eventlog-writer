package de.zalando.jpa.springframework;

import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

/**
 * This {@link EclipseLinkJpaVendorAdapter} is intended to use our {@link ExtendedEclipseLinkJpaDialect}.<br/>
 * At time of writing we do not find a 'setter' for the 'jpaDialect'.
 *
 * @author  jbellmann
 */
public class ExtendedEclipseLinkJpaVendorAdapter extends EclipseLinkJpaVendorAdapter {

    private JpaDialect jpaDialect = new ExtendedEclipseLinkJpaDialect();

    /**
     * Returns our modified {@link JpaDialect}.
     *
     * @see  {@link ExtendedEclipseLinkJpaDialect}
     */
    @Override
    public JpaDialect getJpaDialect() {
        return this.jpaDialect;
    }

}
