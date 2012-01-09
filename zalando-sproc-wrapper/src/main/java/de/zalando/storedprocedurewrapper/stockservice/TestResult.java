package de.zalando.storedprocedurewrapper.stockservice;

import com.typemapper.annotations.DatabaseField;

/**
 * @author  jmussler
 */

public class TestResult {
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

    public TestResult() { }

    public TestResult(final String _a, final String _b) {
        a = _a;
        b = _b;
    }

}
