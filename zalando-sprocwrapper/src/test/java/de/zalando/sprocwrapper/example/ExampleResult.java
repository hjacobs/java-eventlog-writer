package de.zalando.sprocwrapper.example;

import com.typemapper.annotations.DatabaseField;

/**
 * @author  jmussler
 */

public class ExampleResult {
    @DatabaseField(name = "a")
    public String a;

    @DatabaseField(name = "b")
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

    public ExampleResult() { }

    public ExampleResult(final String _a, final String _b) {
        a = _a;
        b = _b;
    }

}
