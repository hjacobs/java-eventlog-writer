package de.zalando.sprocwrapper.example;

import com.typemapper.annotations.DatabaseField;
import com.typemapper.annotations.DatabaseType;

import de.zalando.sprocwrapper.example.transformer.StringTransformer;

/**
 * @author  jmussler
 */
@DatabaseType
public class ExampleDomainObjectWithSimpleTransformer {

    @DatabaseField(transformer = StringTransformer.class)
    public String a;

    @DatabaseField(name = "b", transformer = StringTransformer.class)
    public String b;

    public String getA() {
        return a;
    }

    public void setA(final String a) {
        this.a = a;
    }

    public String getB() {
        return b;
    }

    public void setB(final String b) {
        this.b = b;
    }

    public ExampleDomainObjectWithSimpleTransformer() { }

    public ExampleDomainObjectWithSimpleTransformer(final String _a, final String _b) {
        a = _a;
        b = _b;
    }
}
