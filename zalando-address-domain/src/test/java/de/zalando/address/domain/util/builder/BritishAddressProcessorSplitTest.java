package de.zalando.address.domain.util.builder;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.address.domain.util.builder.processor.BritishAddressProcessor;

import junit.framework.Assert;

@RunWith(value = Parameterized.class)
public class BritishAddressProcessorSplitTest {

    private final String raw;

    private final String expectedStreet;
    private final String expectedHouseNumber;
    private final String expectedHouseName;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {

            {"Castle Exchange 41 Broad Street", "Broad Street", "41", "Castle Exchange"},
            {"Castle Exchange 41A Broad Street", "Broad Street", "41A", "Castle Exchange"},
            {"Lonsdale Works, Gibson Street", "Gibson Street", null, "Lonsdale Works"},
            {
                "Active Marketing, Unit 4 Zephyr House, Calleva Park", "Calleva Park", null,
                "Active Marketing, Unit 4 Zephyr House"
            },
            {
                "Lola Print Services, Unit 4a Zephyr House, Calleva Park", "Calleva Park", null,
                "Lola Print Services, Unit 4a Zephyr House"
            },
            {"20 Queen Square Apartments, Bell Avenue", "Bell Avenue", null, "20 Queen Square Apartments"},
            {"1 Kitchingham Farm Cottages, Sheepstreet Lane", "Sheepstreet Lane", null, "1 Kitchingham Farm Cottages"},
            {
                "1 Kitchingham Farm Cottages, 10 Sheepstreet Lane", "Sheepstreet Lane", "10",
                "1 Kitchingham Farm Cottages"
            },
            {
                "1 Kitchingham Farm Cottages, 10A Sheepstreet Lane", "Sheepstreet Lane", "10A",
                "1 Kitchingham Farm Cottages"
            },
            {
                "Mark Woods Gems Unit 29, Arundel House 36-43 Kirby Street", "Kirby Street", "36-43",
                "Mark Woods Gems Unit 29, Arundel House"
            },
            {
                "Mark Woods Gems Unit 29, Arundel House 36-43A Kirby Street", "Kirby Street", "36-43A",
                "Mark Woods Gems Unit 29, Arundel House"
            }
        };
        return Arrays.asList(data);
    }

    public BritishAddressProcessorSplitTest(final String raw, final String street, final String number,
            final String houseName) {
        this.raw = raw;
        this.expectedStreet = street;
        this.expectedHouseNumber = number;
        this.expectedHouseName = houseName;
    }

    @Test
    public void testCorrectSplitAddress1() {

        String street = BritishAddressProcessor.guessStreetName(raw);
        String number = BritishAddressProcessor.guessHouseNumber(raw);
        String houseName = BritishAddressProcessor.guessHouseName(raw);

        Assert.assertEquals(this.expectedStreet, street);
        Assert.assertEquals(this.expectedHouseNumber, number);
        Assert.assertEquals(this.expectedHouseName, houseName);

    }
}
