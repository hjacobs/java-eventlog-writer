package de.zalando.address.domain.checker;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import de.zalando.address.domain.CustomerAddress;

import de.zalando.domain.address.Similarity;

@XmlType(propOrder = {"splittedStreet", "similarity"})
public class CheckedDeliveryAddress extends CustomerAddress {

    private static final long serialVersionUID = 2402157365589152386L;

    private Street splittedStreet;

    private Similarity similarity;

    @XmlElement(name = "splittedStreet", nillable = false, required = false)
    public Street getSplittedStreet() {
        return splittedStreet;
    }

    public void setSplittedStreet(final Street splittedStreet) {
        this.splittedStreet = splittedStreet;
    }

    public void setSplittedStreet(final String name, final String number) {
        this.splittedStreet = new Street(name, number);
    }

    @XmlElement(name = "similarity", nillable = false, required = false)
    public Similarity getSimilarity() {
        return similarity;
    }

    public void setSimilarity(final Similarity similarity) {
        this.similarity = similarity;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CheckedDeliveryAddress [splittedStreet=");
        builder.append(splittedStreet);
        builder.append(", similarity=");
        builder.append(similarity);
        builder.append("]");
        return builder.toString();
    }

}
