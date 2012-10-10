package de.zalando.address.domain.checker;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"name", "number"})
public class Street implements Serializable {

    private static final long serialVersionUID = 590545546539118861L;

    private String name;

    private String number;

    public Street() { }

    public Street(final String name, final String number) {
        this.name = name;
        this.number = number;
    }

    @XmlElement(name = "name", nillable = false, required = false)
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    @XmlElement(name = "number", nillable = false, required = false)
    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        result = prime * result;

        if (name != null) {
            result += name.hashCode();
        }

        result = prime * result;

        if (number != null) {
            result += number.hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Street other = (Street) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (number == null) {
            if (other.number != null) {
                return false;
            }
        } else if (!number.equals(other.number)) {
            return false;
        }

        return true;
    }

}
