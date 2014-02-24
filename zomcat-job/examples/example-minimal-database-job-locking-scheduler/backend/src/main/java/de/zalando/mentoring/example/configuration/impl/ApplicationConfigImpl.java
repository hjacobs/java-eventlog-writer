package de.zalando.mentoring.example.configuration.impl;

/**
 * @author  danieldelhoyo Date: 2/22/13 Time: 5:28 PM
 */
import java.io.Serializable;

import org.springframework.stereotype.Component;

import de.zalando.zomcat.appconfig.BaseApplicationConfig;
import de.zalando.zomcat.appconfig.BaseApplicationConfigImpl;

@Component(BaseApplicationConfig.BEAN_NAME)
public class ApplicationConfigImpl extends BaseApplicationConfigImpl implements Serializable {
    private static final long serialVersionUID = 1L;

}
