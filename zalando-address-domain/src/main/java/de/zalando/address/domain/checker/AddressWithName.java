package de.zalando.address.domain.checker;

import java.io.Serializable;

import de.zalando.domain.address.Address;
import de.zalando.domain.address.Gender;

public class AddressWithName implements Serializable {

    private static final long serialVersionUID = 1L;

    private String firstName;
    private String lastName;
    private Gender gender;
    private Address address;

    /**
     * Default constructor that leaves all fields <code>null</code>.
     */
    public AddressWithName() {
        super();
    }

    /**
     * Convenience constructor that initializes all fields.
     */
    public AddressWithName(final Address address, final String firstName, final String lastName, final Gender gender) {
        this();
        this.address = address;
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public final void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public final void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public final Address getAddress() {
        return address;
    }

    public final void setAddress(final Address address) {
        this.address = address;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(final Gender gender) {
        this.gender = gender;
    }

    public void setGender(final de.zalando.customer.webservice.service.Gender gender) {
        this.gender = Gender.FEMALE;
        if (gender == de.zalando.customer.webservice.service.Gender.MALE) {
            this.gender = Gender.MALE;
        }
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        builder.append("AddressWithName [firstName=");
        builder.append(firstName);
        builder.append(", lastName=");
        builder.append(lastName);
        builder.append(", gender=");
        builder.append(gender);
        builder.append(", address=");
        builder.append(address);
        builder.append("]");
        return builder.toString();
    }

}
