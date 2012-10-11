package de.zalando.address.domain.util.builder.processor;

import static org.hamcrest.core.Is.is;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import org.junit.runner.RunWith;

import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.zalando.address.domain.util.builder.AddressBuilder;

import de.zalando.domain.address.Address;
import de.zalando.domain.globalization.ISOCountryCode;

@RunWith(value = Parameterized.class)
public class DutchAddressProcessorGuessTest {

    private final String city;

    private final String zip;

    private final String streetName;

    private final String expectedCity;

    private final String expectedZip;

    private final String expectedName;

    private final String expectedNr;

    private final String expectedAdditional;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {

            // @formatter:off
            {"den haag", "1234AB", "De 13e Med 230", "'s-Gravenhage", "1234 AB", "De 13e Med", "230", null},
            {
                "den haag", "1234AB", "Pad van 16 april 1944, 230", "'s-Gravenhage", "1234 AB", "Pad Van 16 April 1944",
                "230", null
            },
            {
                "den haag", "1234AB", "Pad van 16 april 1944 230", "'s-Gravenhage", "1234 AB", "Pad Van 16 April 1944",
                "230", null
            },
            {"den haag", "1234AB", "West Weer 230", "'s-Gravenhage", "1234 AB", "West Weer", "230", null},
            {
                "den haag", "1234        AB", "Ernest Staesstr   . 230", "'s-Gravenhage", "1234 AB",
                "Ernest Staesstraat", "230", null
            },
            {
                "den haag", "1234 --- \\\\ ---- \\/\\/\\/  AB", "Ernest Staesstraat 230", "'s-Gravenhage", "1234 AB",
                "Ernest Staesstraat", "230", null
            },
            {"den haag", "1234AB", "St Olofspoort 230", "'s-Gravenhage", "1234 AB", "Sint Olofspoort", "230", null},
            {"den haag", "1234AB", "St. Olofspoort 230", "'s-Gravenhage", "1234 AB", "Sint Olofspoort", "230", null},
            {"den haag", "1234AB", "St  . Olofspoort 230", "'s-Gravenhage", "1234 AB", "Sint Olofspoort", "230", null},
            {
                "den haag", "1234AB", "Laan van Westerkappeln 230", "'s-Gravenhage", "1234 AB",
                "Laan Van Westerkappeln", "230", null
            },
            {
                "den haag", "1234AB", "Laan van Schondeln. 230", "'s-Gravenhage", "1234 AB", "Laan Van Schondeln.",
                "230", null
            },
            {
                "den haag", "1234AB", "Laan van Schondeln 230", "'s-Gravenhage", "1234 AB", "Laan Van Schondeln", "230",
                null
            },
            {
                "den haag", "1234AB", "Burg Vening Meineszln  . 230", "'s-Gravenhage", "1234 AB",
                "Burgemeester Vening Meineszlaan", "230", null
            },
            {
                "den haag", "1234AB", "Burg Vening Meineszln 230", "'s-Gravenhage", "1234 AB",
                "Burgemeester Vening Meineszlaan", "230", null
            },
            {
                "den haag", "1234AB", "Burg Vening Meineszlaan 230", "'s-Gravenhage", "1234 AB",
                "Burgemeester Vening Meineszlaan", "230", null
            },
            {
                "den haag", "1234AB", "Burg   .  Vening Meineszlaan 230", "'s-Gravenhage", "1234 AB",
                "Burgemeester Vening Meineszlaan", "230", null
            },
            {
                "den haag", "1234AB", "Burg Vening Meineszlaan 230", "'s-Gravenhage", "1234 AB",
                "Burgemeester Vening Meineszlaan", "230", null
            },
            {"den haag", "1234AB", "Weg 1940-1945 230", "'s-Gravenhage", "1234 AB", "Weg 1940-1945", "230", null},
            {"den haag", "1234AB", "Boulevard 1945 230", "'s-Gravenhage", "1234 AB", "Boulevard 1945", "230", null},
            {"den haag", "1234AB", "Boulevard 1945, 230", "'s-Gravenhage", "1234 AB", "Boulevard 1945", "230", null},
            {"den haag", "1234AB", "Boulevard 1945. 230", "'s-Gravenhage", "1234 AB", "Boulevard 1945.", "230", null},
            {
                "den haag", "1234AB", "M. L Kingstr 67", "'s-Gravenhage", "1234 AB", "Martin Luther Kingstraat", "67",
                null
            },
            {
                "den haag", "1234AB", "M L Kingstr 67", "'s-Gravenhage", "1234 AB", "Martin Luther Kingstraat", "67",
                null
            },
            {
                "den haag", "1234AB", "M. L. Kingstr 67", "'s-Gravenhage", "1234 AB", "Martin Luther Kingstraat", "67",
                null
            },
            {
                "den haag", "1234AB", "M   . L   . Kingstr 67", "'s-Gravenhage", "1234 AB", "Martin Luther Kingstraat",
                "67", null
            },
            {
                "den haag", "1234AB", "M L   . Kingstr 67", "'s-Gravenhage", "1234 AB", "Martin Luther Kingstraat",
                "67", null
            },
            {
                "den haag", "1234AB", "M Luther Kingstr 67", "'s-Gravenhage", "1234 AB", "Martin Luther Kingstraat",
                "67", null
            },
            {
                "den haag", "1234AB", "M   . Luther Kingstr 67", "'s-Gravenhage", "1234 AB", "Martin Luther Kingstraat",
                "67", null
            },
            {
                "den haag", "1234AB", "G  . Van Oostenstr 67", "'s-Gravenhage", "1234 AB", "Geertruyt Van Oostenstraat",
                "67", null
            },
            {
                "den haag", "1234AB", "G Van Oostenstr 67", "'s-Gravenhage", "1234 AB", "Geertruyt Van Oostenstraat",
                "67", null
            },
            {"den haag", "1234AB", "G Van Gstraat 67", "'s-Gravenhage", "1234 AB", "G Van Gstraat", "67", null},
            {
                "den haag", "1234AB", "Waldeck Pyrmontkade 884", "'s-Gravenhage", "1234 AB", "Waldeck Pyrmontkade",
                "884", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1=-/A, c/O Herr Frau", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1a", "c/O Herr Frau"
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1=-/A, c/o Herr Frau", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1a", "c/o Herr Frau"
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1=-/A c/o Herr Frau", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1a", "c/o Herr Frau"
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Hauptstr. 1a 4 Og .rechts", "'s-Hertogenbosch", "123A B4",
                "1e Hauptstraat", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Frankfurt (Oder) 5", "'s-Hertogenbosch", "123A B4", "Frankfurt (Oder)",
                "5", null
            },
            {"den bosch", "123$%^&A$B4 5", "Strasse 2345 5", "'s-Hertogenbosch", "123A B4", "Strasse 2345", "5", null},
            {
                "den bosch", "123$%^&A$B4 5", "straße 246   16a", "'s-Hertogenbosch", "123A B4", "Straße 246", "16a",
                null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Strasse 2345 , 5", "'s-Hertogenbosch", "123A B4", "Strasse 2345", "5",
                null
            },
            {"den bosch", "123$%^&A$B4 5", "Straße 2345 5", "'s-Hertogenbosch", "123A B4", "Straße 2345", "5", null},
            {"den bosch", "123$%^&A$B4 5", "strasse 2345 5", "'s-Hertogenbosch", "123A B4", "Strasse 2345", "5", null},
            {"den bosch", "123$%^&A$B4 5", "straße 2345 5", "'s-Hertogenbosch", "123A B4", "Straße 2345", "5", null},
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 10\\00\\1", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "10/1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1=-/A", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1-A", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 00//001", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1/0/0/00", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1.", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1a", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1A", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 A", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1/A", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 / A", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 / a", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht, 1a", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 2 /  3", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1 2/3", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 2 /  3   A", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1 2/3a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 2 /  3A", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1 2/3a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 2 /  3a", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1 2/3a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1-12.", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1-12", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1/12.", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1/12", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1/", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht /1", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht ////1", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1//////", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 01", "'s-Hertogenbosch", "123A B4", "Binnenvestgracht",
                "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 00001", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1-----", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1&&&&", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1%^&$#$%&", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht %%%1", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht ----1----", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht ----1", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 00001///", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht ///00001///", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht ///00001", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht ///00001", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1/12/14.", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 / 12   /  14.", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 //// 12   ///  14.", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 / / // 12   ///  14.", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 / / // 12   --  14.", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 / / &&%// 12   --  14.", "'s-Hertogenbosch",
                "123A B4", "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 / / !@#$%^&* // 12   --  14.", "'s-Hertogenbosch",
                "123A B4", "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 \\\\ \\  12   \\ --  14.", "'s-Hertogenbosch",
                "123A B4", "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 \\\\ \\  12   \\ / /  14.", "'s-Hertogenbosch",
                "123A B4", "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 1 -/ 12   /-  14.", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Straße des 17. Juni 135", "'s-Hertogenbosch", "123A B4",
                "Straße Des 17. Juni", "135", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Straße des 17 Juni 135", "'s-Hertogenbosch", "123A B4",
                "Straße Des 17 Juni", "135", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Straße des 17 Juni 135 A", "'s-Hertogenbosch", "123A B4",
                "Straße Des 17 Juni", "135a", null
            },
            {"den bosch", "123$%^&A$B4 5", "C8 19", "'s-Hertogenbosch", "123A B4", "C8", "19", null},
            {"den bosch", "123$%^&A$B4 5", "C8,19", "'s-Hertogenbosch", "123A B4", "C8", "19", null},
            {"den bosch", "123$%^&A$B4 5", "C8 , 19", "'s-Hertogenbosch", "123A B4", "C8", "19", null},
            {"den bosch", "123$%^&A$B4 5", "C 8 , 19", "'s-Hertogenbosch", "123A B4", "C 8", "19", null},
            {"den bosch", "123$%^&A$B4 5", "C 8, 19", "'s-Hertogenbosch", "123A B4", "C 8", "19", null},
            {"den bosch", "123$%^&A$B4 5", "C8. 19", "'s-Hertogenbosch", "123A B4", "C8.", "19", null},
            {"den bosch", "123$%^&A$B4 5", "C8 -> 19", "'s-Hertogenbosch", "123A B4", "C8", "19", null},
            {"den bosch", "123$%^&A$B4 5", "C8 - 19", "'s-Hertogenbosch", "123A B4", "C8", "19", null},
            {
                "den bosch", "123$%^&A$B4 5", "Frankfurt (Oder) 5", "'s-Hertogenbosch", "123A B4", "Frankfurt (Oder)",
                "5", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11 / A 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11 4 Og .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11 4 og .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11, 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11. 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11., 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11., 4OG .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11., 4. OG .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11., OG 4 .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Binnenvestgracht 11., 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Alfred-Delp-Straße 2", "'s-Hertogenbosch", "123A B4",
                "Alfred-Delp-Straße", "2", null
            },
            {"den bosch", "123$%^&A$B4 5", "5.Wallstraße 9", "'s-Hertogenbosch", "123A B4", "5.Wallstraße", "9", null},
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1=-/A", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1-A", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 00//001", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1/0/0/00", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1.", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1a", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1A", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 A", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1/A", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 / A", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 / a", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht, 1a", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 2 /  3", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1 2/3", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 2 /  3   A", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1 2/3a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 2 /  3A", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1 2/3a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 2 /  3a", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1 2/3a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1-12.", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1-12", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1/12.", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1/12", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1/", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht /1", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht ////1", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1//////", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 01", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 00001", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1-----", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1&&&&", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1%^&$#$%&", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht %%%1", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht ----1----", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht ----1", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 00001///", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht ///00001///", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht ///00001", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht ///00001", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1/12/14.", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 / 12   /  14.", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 //// 12   ///  14.", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 / / // 12   ///  14.", "'s-Hertogenbosch",
                "123A B4", "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 / / // 12   --  14.", "'s-Hertogenbosch",
                "123A B4", "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 / / &&%// 12   --  14.", "'s-Hertogenbosch",
                "123A B4", "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 / / !@#$%^&* // 12   --  14.", "'s-Hertogenbosch",
                "123A B4", "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 \\\\ \\  12   \\ --  14.", "'s-Hertogenbosch",
                "123A B4", "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 \\\\ \\  12   \\ / /  14.", "'s-Hertogenbosch",
                "123A B4", "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "1e Binnenvestgracht 1 -/ 12   /-  14.", "'s-Hertogenbosch", "123A B4",
                "1e Binnenvestgracht", "1/12/14", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11 / A 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11a", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11 4 Og .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11 4 og .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11, 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11. 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11., 4 OG .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11., 4OG .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11., 4. OG .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "2e Binnenvestgracht 11., OG 4 .rechts", "'s-Hertogenbosch", "123A B4",
                "2e Binnenvestgracht", "11", null
            },
            {
                "den bosch", "123$%^&A$B4 5", "Weedeweg Z.Z. 19", "'s-Hertogenbosch", "123A B4", "Weedeweg Z.Z.", "19",
                null
            },
            {"den bosch", "123$%^&A$B4 5", "12345", "'s-Hertogenbosch", "123A B4", "", "12345", null},
            {"den bosch", "123$%^&A$B4 5", null, "'s-Hertogenbosch", "123A B4", "", null, null},
            {"den bosch", "123$%^&A$B4 5", "", "'s-Hertogenbosch", "123A B4", "", null, null},
            {"den bosch", "1236AA", "", "'s-Hertogenbosch", "1236 AA", "", null, null},
            {"den bosch", "123$%^&A$B4 5", "", "'s-Hertogenbosch", "123A B4", "", null, null}
        };

        // @formatter:on
        return Arrays.asList(data);
    }

    public DutchAddressProcessorGuessTest(final String city, final String zip, final String streetName,
            final String expectedCity, final String expectedZip, final String expectedName, final String expectedNr,
            final String expectedAdditional) {
        this.city = city;
        this.zip = zip;
        this.streetName = streetName;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedName = expectedName;
        this.expectedNr = expectedNr;
        this.expectedAdditional = expectedAdditional;
    }

    @Test
    public void testGuessStreetNumber() throws Exception {

        // for (int i = 0; i < 2500; ++i) {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.NL).city(city).zip(zip)
                                              .streetWithHouseNumber(streetName).build();
        assertThat(String.format("orig street [%s] street name", streetName), address.getStreetName(),
            is(expectedName));
        assertThat(String.format("orig street [%s] house number", streetName), address.getHouseNumber(),
            is(expectedNr));
        assertThat(String.format("orig street [%s] additional", streetName), address.getAdditional(),
            is(expectedAdditional));
        assertThat(String.format("orig street [%s] city", streetName), address.getCity(), is(expectedCity));
        assertThat(String.format("orig street [%s] zip", streetName), address.getZip(), is(expectedZip));
        // }
    }

}
