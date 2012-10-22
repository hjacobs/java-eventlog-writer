package de.zalando.payment.domain.settings;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"code", "name"})
public class BankGroupType {

    private String code;
    private String name;

    public BankGroupType() { }

    public BankGroupType(final String code, final String name) {
        this.code = code;
        this.name = name;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlElement(name = "code", nillable = false, required = true)
    public String getCode() {
        return code;
    }

    @XmlElement(name = "name", nillable = false, required = true)
    public String getName() {
        return name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BankGroupType that = (BankGroupType) o;

        if (code != null ? !code.equals(that.code) : that.code != null) {
            return false;
        }

        if (name != null ? !name.equals(that.name) : that.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "BankGroupType{" + "code='" + code + '\'' + ", name='" + name + '\'' + '}';
    }
}
