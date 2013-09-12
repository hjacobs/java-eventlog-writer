package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;

public class Example1DomainObject1 {
    @DatabaseField
    private String example1Field1;

    @DatabaseField
    private Example1DomainObject2 example1Field2;

    public String getExample1Field1() {
        return example1Field1;
    }

    public void setExample1Field1(final String example1Field1) {
        this.example1Field1 = example1Field1;
    }

    public Example1DomainObject2 getExample1Field2() {
        return example1Field2;
    }

    public void setExample1Field2(final Example1DomainObject2 example1Field2) {
        this.example1Field2 = example1Field2;
    }

}
