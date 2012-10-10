package de.zalando.address.domain.checker;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {"checkerIds", "fallbackCheckerIds"})
public class CheckerIds {

    private String[] checkerIds = new String[0];

    private String[] fallbackCheckerIds = new String[0];

    @XmlElement(name = "checkerId", required = true, nillable = false)
    public String[] getCheckerIds() {
        return checkerIds;
    }

    public void setCheckerIds(final String[] checkerIds) {
        this.checkerIds = checkerIds;
    }

    @XmlElement(name = "fallbackCheckerId", required = false, nillable = false)
    public String[] getFallbackCheckerIds() {
        return fallbackCheckerIds;
    }

    public void setFallbackCheckerIds(final String[] fallbackCheckerIds) {
        this.fallbackCheckerIds = fallbackCheckerIds;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CheckerIds [checkerIds=");
        builder.append(Arrays.toString(checkerIds));
        builder.append(", fallbackCheckerIds=");
        builder.append(Arrays.toString(fallbackCheckerIds));
        builder.append("]");
        return builder.toString();
    }
}
