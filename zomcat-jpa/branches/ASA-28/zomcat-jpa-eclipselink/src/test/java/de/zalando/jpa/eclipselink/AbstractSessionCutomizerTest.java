package de.zalando.jpa.eclipselink;

import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.config.SessionCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.DatabaseSessionImpl;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.sessions.Login;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;

import org.mockito.Mockito;

import com.google.common.collect.Maps;

/**
 * @author  jbellmann
 */
public abstract class AbstractSessionCutomizerTest {

    public void testCustomizer() throws Exception {
        SessionCustomizer defaultSessionCustomizer = getSessionCustomizer();

        DatabaseField field = new DatabaseField();

// field.se

        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setAttributeName("brandCode");
        mapping.setField(field);
        mapping.setAttributeClassification(String.class);

        ClassDescriptor classDescriptor = new ClassDescriptor();
        classDescriptor.setJavaClass(AttributeHolderBean.class);
        mapping.setDescriptor(classDescriptor);

        Vector mappings = new Vector();
        mappings.add(mapping);

        ClassDescriptor descriptor = new ClassDescriptor();
        descriptor.setMappings(mappings);
        descriptor.setTableName("purchase_order_head");

        Map<Class, ClassDescriptor> result = Maps.newHashMap();

        result.put(Order.class, descriptor);

        Login login = Mockito.mock(Login.class);
        Project project = new Project();
        project.setDescriptors(result);
        project.setDatasourceLogin(login);

        Session session = new DatabaseSessionImpl(project);
        session.setSessionLog(new Slf4jSessionLog());

        //
        defaultSessionCustomizer.customize(session);
    }

    static final class Order { }

    abstract SessionCustomizer getSessionCustomizer();

}
