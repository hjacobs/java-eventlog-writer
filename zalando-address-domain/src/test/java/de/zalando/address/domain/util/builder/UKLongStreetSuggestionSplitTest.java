package de.zalando.address.domain.util.builder;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.address.domain.suggestion.AddressSuggestion;

@RunWith(value = Parameterized.class)
public class UKLongStreetSuggestionSplitTest {

    private final String street;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {

            {"ZIZI, IVORY HOUSE"},
            {"MICHAEL BURNS & CO, 19 MALONE COURT"},
            {"1ST STUDLEY SCOUT HEADQUARTERS, HIGH STREET"},
            {"BRENT MACINTOSH ASSOCIATES, 30 BELMONT HOUSE, HIGH STREET"},
            {"CERTIFIED INTERNATIONAL SYSTEMS LTD, 30 BELMONT HOUSE, HIGH STREET"},
            {"NEIL WILLIES INSURANCE BROKERS LTD, 20 HIGH STREET"},
            {"THE NAIL & BEAUTY BOX, 1 LEES HALL CHAMBERS, MINCING LANE"},
            {"SOUTHERN CROSS DENTAL LAB, 28 DUNGANNON STREET"},
            {"PERFECT SOLUTIONS, UNIT 16, 80-84, ST. MARY ROAD"},
            {"WALTHAMSTOW OSTEOPATHY & NATURAL HEALTH CENTRE, 72 ST. MARY ROAD"},
            {"A GORMLEY VEHICLE REFINISHING, 23-25, BARDSLEY ROAD, EARLSTREES INDUSTRIAL ESTATE"},
            {"SECURITY & RACKING SYSTEMS, 19-20, BARDSLEY ROAD, EARLSTREES INDUSTRIAL ESTATE"}
        };

        return Arrays.asList(data);
    }
    ;

    public UKLongStreetSuggestionSplitTest(final String street) {
        this.street = street;
    }

    @Test
    public void testSuggestionsSplit() {

        AddressSuggestion suggestion = new AddressSuggestion();

        suggestion.setStreet1(this.street);

        suggestion.splitLongStreet(45);

        String street1 = suggestion.getStreet1().trim();
        String street2 = suggestion.getStreet2();

        Assert.assertFalse(street1.endsWith(","));
        if (street2 != null) {
            Assert.assertFalse(street1.startsWith(","));

            Assert.assertEquals(this.street, street1 + ", " + street2);

        } else {
            Assert.assertEquals(this.street, street1);
        }

        System.out.println(this.street + " -> ");
        System.out.println("\t" + street1 + " : " + street2);

    }
}
