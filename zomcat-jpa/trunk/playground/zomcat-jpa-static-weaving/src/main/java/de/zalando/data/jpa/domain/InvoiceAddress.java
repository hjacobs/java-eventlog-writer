package de.zalando.data.jpa.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.base.Objects;

@Entity
@Table(name = "invoice_address")
public class InvoiceAddress implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "invoice_address_id_seq")
    private Integer id;

    private String firstname;

    private String lastname;

    private String city;

    private String postcode;

    private String street;

    public Integer getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(final String lastname) {
        this.lastname = lastname;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(final String postcode) {
        this.postcode = postcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("id", id).add("firstname", firstname).add("lastname", lastname)
                      .toString();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InvoiceAddress that = (InvoiceAddress) o;

        if (city != null ? !city.equals(that.city) : that.city != null) {
            return false;
        }

        if (firstname != null ? !firstname.equals(that.firstname) : that.firstname != null) {
            return false;
        }

        if (lastname != null ? !lastname.equals(that.lastname) : that.lastname != null) {
            return false;
        }

        if (postcode != null ? !postcode.equals(that.postcode) : that.postcode != null) {
            return false;
        }

        if (street != null ? !street.equals(that.street) : that.street != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = firstname != null ? firstname.hashCode() : 0;
        result = 31 * result + (lastname != null ? lastname.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (postcode != null ? postcode.hashCode() : 0);
        result = 31 * result + (street != null ? street.hashCode() : 0);
        return result;
    }
}
