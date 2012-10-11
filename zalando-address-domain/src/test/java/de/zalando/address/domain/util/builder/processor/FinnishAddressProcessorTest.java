package de.zalando.address.domain.util.builder.processor;

import static org.hamcrest.CoreMatchers.equalTo;

import static org.junit.Assert.assertThat;

import static org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;

import de.zalando.address.domain.util.builder.AddressBuilder;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

@RunWith(value = Parameterized.class)
public class FinnishAddressProcessorTest {

    private String city;
    private String zip;
    private String addressLine;
    private String expectedCity;
    private String expectedZip;
    private final String expectedStreet;
    private final String expectedHouseNumber;
    private String expectedAdditional;

    @Parameters
    public static List<String[]> getParameters() {
        final String[][] params = new String[][] {
            //J-
            // examples "from the book"
            {"Helsinki", "00550", "Mäkelänkatu 25 B 13",
             "Helsinki", "00550", "Mäkelänkatu", "25 B 13", null
            },
            {"Helsinki", "00280", "c/o Mikko Manninen, Mannerheimintie 97a A 52",
             "Helsinki", "00280", "Mannerheimintie", "97a A 52", "c/o Mikko Manninen"
            },
            {"JYVÄSKYLÄ", "40100", "Kauppakatu 25-27",
             "Jyväskylä", "40100", "Kauppakatu", "25-27", null
            },
            {"HELSINKI", "FI-00550", "Mäkelänkatu 25 D 21",
             "Helsinki", "00550", "Mäkelänkatu", "25 D 21", null
            },
            {"HELSINKI", "FI-00350", "Ulvilantie 29/4 K 825",
             "Helsinki", "00350", "Ulvilantie", "29/4 K 825", null
            },
            {"NURMO", "60550", "Metsäpellontie 598",
             "Nurmo", "60550", "Metsäpellontie", "598", null
            },
            {"MARIEHAMN", "22100", "Postgränd 8b",
             "Mariehamn", "22100", "Postgränd", "8b", null
            },

            // random examples
            {"Helsinki", "00550", "Mäkelänkatu 25A",
             "Helsinki", "00550", "Mäkelänkatu", "25a", null
            },
            {"Helsinki", "00550", "Mäkelänkatu 25A",
             "Helsinki", "00550", "Mäkelänkatu", "25a", null
            },
            {"Helsinki", "00550", "Mäkelänkatu 25a A",
             "Helsinki", "00550", "Mäkelänkatu", "25a A", null
            },
            {"Helsinki", "00550", "Mäkelänkatu 25a A 345",
             "Helsinki", "00550", "Mäkelänkatu", "25a A 345", null
            },
            {"Helsinki", "00550", "Mäkelänkatu 25 C 2B",
             "Helsinki", "00550", "Mäkelänkatu", "25 C 2b", null
            },
            {"Helsinki", "00550", "Mäkelänkatu 25 as. 2",
             "Helsinki", "00550", "Mäkelänkatu", "25 as. 2", null
            },
            {"Helsinki", "00550", "Mäkelänkatu 25 as 2",
             "Helsinki", "00550", "Mäkelänkatu", "25 as. 2", null
            },
            {"Helsinki", "00550", "Mäkelänkatu 25 bst. 2",
             "Helsinki", "00550", "Mäkelänkatu", "25 as. 2", null
            },
            {"Helsinki", "00550", "Mäkelänkatu 25 bst 2",
             "Helsinki", "00550", "Mäkelänkatu", "25 as. 2", null
            },
            {"Helsinki", "00550", "Ulvilantie 29/4 K 825",
             "Helsinki", "00550", "Ulvilantie", "29/4 K 825", null
            },
            {"Helsinki", "00550", "Ulvilantie 29/4a K bst 825",
             "Helsinki", "00550", "Ulvilantie", "29/4a K as. 825", null
            },
            {"Helsinki", "00550", "Pasilankatu 2 Zalando Offline Outlet",
             "Helsinki", "00550", "Pasilankatu", "2", "Zalando Offline Outlet"
            },
            {"Helsinki", "00550", "Pasilankatu 2, Zalando Offline Outlet",
             "Helsinki", "00550", "Pasilankatu", "2", "Zalando Offline Outlet"
            },
            //J+
        };
        return Arrays.asList(params);
    }

    public FinnishAddressProcessorTest(final String city, final String zip, final String addressLine,
            final String expectedCity, final String expectedZip, final String expectedStreet,
            final String expectedHouseNumber, final String expectedAdditional) {
        this.city = city;
        this.zip = zip;
        this.addressLine = addressLine;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedStreet = expectedStreet;
        this.expectedHouseNumber = expectedHouseNumber;
        this.expectedAdditional = expectedAdditional;
    }

    @Test
    public void shouldNormalizeAddress() {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.FI).city(city).zip(zip)
                                              .streetWithHouseNumber(addressLine).build();
        assertThat(address.getStreetName(), equalTo(expectedStreet));
        assertThat(address.getHouseNumber(), equalTo(expectedHouseNumber));
        assertThat(address.getZip(), equalTo(expectedZip));
        assertThat(address.getCity(), equalTo(expectedCity));
        assertThat(address.getAdditional(), equalTo(expectedAdditional));
    }

}
