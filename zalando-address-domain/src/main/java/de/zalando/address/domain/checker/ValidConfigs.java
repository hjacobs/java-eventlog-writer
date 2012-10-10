package de.zalando.address.domain.checker;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

import de.zalando.utils.Pair;

@XmlType(propOrder = {"addressCheckers", "addressCheckerDecorators"})
public class ValidConfigs {

    private String[] addressCheckers;

    private String[] addressCheckerDecorators;

    public ValidConfigs() { }

    public ValidConfigs(final Pair<String[], String[]> validConfigs) {
        if (validConfigs != null) {
            addressCheckers = validConfigs.getFirst();
            addressCheckerDecorators = validConfigs.getSecond();
        }
    }

    @XmlElementWrapper(name = "addressCheckers")
    @XmlElement(name = "addressChecker", required = true, nillable = false)
    public String[] getAddressCheckers() {
        return addressCheckers;
    }

    public void setAddressCheckers(final String[] addressCheckers) {
        this.addressCheckers = addressCheckers;
    }

    @XmlElementWrapper(name = "addressCheckerDecorators")
    @XmlElement(name = "addressCheckerDecorator", required = true, nillable = false)
    public String[] getAddressCheckerDecorators() {
        return addressCheckerDecorators;
    }

    public void setAddressCheckerDecorators(final String[] addressCheckerDecorators) {
        this.addressCheckerDecorators = addressCheckerDecorators;
    }

    @Override
    public String toString() {

        final StringBuilder builder = new StringBuilder();
        builder.append("ValidConfigs [addressCheckers=");
        builder.append(Arrays.toString(addressCheckers));
        builder.append(", addressCheckerDecorators=");
        builder.append(Arrays.toString(addressCheckerDecorators));
        builder.append("]");

        return builder.toString();

    }

}
