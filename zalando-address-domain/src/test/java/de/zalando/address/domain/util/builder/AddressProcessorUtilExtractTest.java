package de.zalando.address.domain.util.builder;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.extractNumber;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.utils.Pair;

@RunWith(value = Parameterized.class)
public class AddressProcessorUtilExtractTest {

    private String streetWithNumber;

    private String number;

    private NumberPosition position;

    private String streetName;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {

            // @formatter:off
            {"Zinnowitzerstr. 1.", "1.", NumberPosition.RIGHT, "Zinnowitzerstr."},
            {"Zinnowitzerstr. 1.  ", "1.", NumberPosition.RIGHT, "Zinnowitzerstr."},
            {"1. Zinnowitzerstr.", "1.", NumberPosition.RIGHT, "Zinnowitzerstr."},
            {"  1. Zinnowitzerstr.", "1.", NumberPosition.RIGHT, "Zinnowitzerstr."},
            {"Straße des 17. Juni 135", "135", NumberPosition.RIGHT, "Straße des 17. Juni"},
            {"135 Straße des 17. Juni", "135", NumberPosition.RIGHT, "Straße des 17. Juni"},
            {"Zinnowitzer Str. 11 4 OG .rechts", "11 4", NumberPosition.RIGHT, "Zinnowitzer Str."},
            {"1 bis rue Chambiges", "1 bis", NumberPosition.LEFT, "rue Chambiges"},
            {"rue Chambiges 1 bis", "1 bis", NumberPosition.LEFT, "rue Chambiges"},
            {"Charles 1 bis rue Chambiges", "1 bis", NumberPosition.LEFT, "rue Chambiges"},
            // @formatter:on
        };
        return Arrays.asList(data);
    }

    public AddressProcessorUtilExtractTest(final String streetWithNumber, final String number,
            final NumberPosition position, final String streetName) {
        this.streetWithNumber = streetWithNumber;
        this.number = number;
        this.position = position;
        this.streetName = streetName;
    }

    @Test
    public void testExtract() throws Exception {
        Pair<String, String> extracted = extractNumber(streetWithNumber, number, position);
        assertThat(extracted.getFirst(), is(streetName));
    }
}
