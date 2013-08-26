package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType
public class BugLookupType {

    @DatabaseField
    public int a;
    @DatabaseField
    public int b;

    public BugLookupType() { }
}
