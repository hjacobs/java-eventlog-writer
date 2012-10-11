package de.zalando.address.domain.util.builder;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.removeSubstring;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class AddressProcessorUtilRemoveTest {

    private final String streetWithNumber;

    private final String number;

    private final String streetName;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {

            // @formatter:off
            {"Charles 1 bis rue Chambiges", "Charles 1 bis rue Chambiges", "Charles 1 bis rue Chambiges"},
            {"   Charles 1 bis rue Chambiges", "Charles 1 bis rue Chambiges", "   Charles 1 bis rue Chambiges"},
            {"Zinnowitzerstr. 1.", null, "Zinnowitzerstr. 1."},
            {"Zinnowitzerstr. 1.", "XXX", "Zinnowitzerstr. 1."},
            {"Zinnowitzerstr. 1.", "1.", "Zinnowitzerstr. "},
            {"Zinnowitzerstr. 1.  ", "1.", "Zinnowitzerstr.   "},
            {"1. Zinnowitzerstr.", "1.", " Zinnowitzerstr."},
            {"  1. Zinnowitzerstr.", "1.", "   Zinnowitzerstr."},
            {"Straße des 17. Juni 135", "135", "Straße des 17. Juni "},
            {"135 Straße des 17. Juni", "135", " Straße des 17. Juni"},
            {"Zinnowitzer Str. 11 4 OG .rechts", "11 4", "Zinnowitzer Str.  OG .rechts"},
            {"1 bis rue Chambiges", "1 bis", " rue Chambiges"},
            {"rue Chambiges 1 bis", "1 bis", "rue Chambiges "},
            {"Charles 1 bis rue Chambiges", "1 bis", "Charles  rue Chambiges"},
            // @formatter:on
        };
        return Arrays.asList(data);
    }

    public AddressProcessorUtilRemoveTest(final String streetWithNumber, final String number, final String streetName) {
        this.streetWithNumber = streetWithNumber;
        this.number = number;
        this.streetName = streetName;
    }

    @Test
    public void testExtract() throws Exception {
        final StringBuilder builder = new StringBuilder(streetWithNumber);
        removeSubstring(builder, number, true);
        assertThat(builder.toString(), is(streetName));
    }
}
