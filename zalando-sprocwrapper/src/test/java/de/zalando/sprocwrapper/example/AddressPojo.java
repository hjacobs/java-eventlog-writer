package de.zalando.sprocwrapper.example;

import com.typemapper.annotations.DatabaseField;
import com.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType(name = "address_type")
public class AddressPojo {

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final int customerId) {
        this.customerId = customerId;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    @DatabaseField(position = 1)
    public Integer id;

    @DatabaseField(position = 2)
    public int customerId;

    @DatabaseField(position = 3)
    public String street;

    @DatabaseField(position = 4)
    public String number;
}
