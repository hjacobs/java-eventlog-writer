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
public class GermanAddressProcessorGuessTest {

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

        //J-
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73., VorDerHaus", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "VorDerHaus"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'S     Allende-Str.' 1/a", "Berlin", "10115", "Salvador-Allende-Str.",
                "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'S   .  -  Allende-Str.' 1/a", "Berlin", "10115",
                "Salvador-Allende-Str.", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'S. Allende-Str.' 1/a", "Berlin", "10115", "Salvador-Allende-Str.",
                "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'S.Allende-Str.' 1/a", "Berlin", "10115", "Salvador-Allende-Str.",
                "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'S.-Allende-Str.' 1/a", "Berlin", "10115", "Salvador-Allende-Str.",
                "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'S-Allende-Str.' 1/a", "Berlin", "10115", "Salvador-Allende-Str.",
                "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Doctor S.-Allende-Str. 1/a", "Berlin", "10115",
                "Dr. Salvador-Allende-Str.", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Doctor S.Allende-Str. 1/a", "Berlin", "10115",
                "Dr. Salvador-Allende-Str.", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Doctor S. Allende-Str. 1/a", "Berlin", "10115",
                "Dr. Salvador-Allende-Str.", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "A.-Daniel-Thaer-Weg 1/a", "Berlin", "10115",
                "Albrecht-Daniel-Thaer-Weg", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'a-Daniel-Thaer-Weg 1/a", "Berlin", "10115",
                "Albrecht-Daniel-Thaer-Weg", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'a Daniel-Thaer-Weg 1/a", "Berlin", "10115",
                "Albrecht-Daniel-Thaer-Weg", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'a.-Dürer-strasse 1/a", "Berlin", "10115", "Albrecht-Dürer-Str.",
                "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'A-Dürer-strasse 1/a", "Berlin", "10115", "Albrecht-Dürer-Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "A Dürer-strasse 1/a", "Berlin", "10115", "Albrecht-Dürer-Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Pf.   Kneipp-Weg 1/a", "Berlin", "10115", "Pfarrer-Kneipp-Weg", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Pf. Kneipp-Weg 1/a", "Berlin", "10115", "Pfarrer-Kneipp-Weg", "1a",
                null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "Pf.Kneipp-Weg 1/a", "Berlin", "10115", "Pfarrer-Kneipp-Weg", "1a", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Pf.-Kneipp-Weg 1/a", "Berlin", "10115", "Pfarrer-Kneipp-Weg", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Pf  -  Kneipp-Weg 1/a", "Berlin", "10115", "Pfarrer-Kneipp-Weg", "1a",
                null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "Pf-Kneipp-Weg 1/a", "Berlin", "10115", "Pfarrer-Kneipp-Weg", "1a", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Bgm Witter-Strasse 1/a", "Berlin", "10115",
                "Bürgermeister-Witter-Str.", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Bgm. Witter-Strasse 1/a", "Berlin", "10115",
                "Bürgermeister-Witter-Str.", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Bgm.-Witter-Strasse 1/a", "Berlin", "10115",
                "Bürgermeister-Witter-Str.", "1a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Doktor-Ruff-Straße 1/a", "Berlin", "10115", "Doktor-Ruff-Str.", "1a",
                null
            }, // this is an exception in the DFSV data
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Doktor-Weis-Platz 1/a", "Berlin", "10115", "Doktor-Weis-Platz", "1a",
                null
            }, // this is an exception in the DFSV data
            {"Berlin$%^##   ", "1011ABC#$%5", "Doktorweg 1/a", "Berlin", "10115", "Doktorweg", "1a", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Doktorweg 1/a", "Berlin", "10115", "Doktorweg", "1a", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "'Bauerndoktor-Gros-Strasse' 1/a", "Berlin", "10115",
                "Bauerndoktor-Gros-Str.", "1a", null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "Doktorstr. 1/a", "Berlin", "10115", "Doktorstr.", "1a", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Doctor-Hahn-Strasse 1/a", "Berlin", "10115", "Dr.-Hahn-Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Doktor-Hahn-Strasse 1/a", "Berlin", "10115", "Dr.-Hahn-Str.", "1a",
                null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "Zur Doctorey 1/a", "Berlin", "10115", "Zur Doctorey", "1a", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Am Strasserfeld 1/a", "Berlin", "10115", "Am Strasserfeld", "1a", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 5, obergeScHoss 5", "Berlin", "10115", "Strasse 2345",
                "5", "obergeScHoss 5"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 5 4 UNterGEScHoss 5", "Berlin", "10115", "Strasse 2345",
                "5", "4 UNterGEScHoss 5"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 5 4 UNTERGEScHoß 5", "Berlin", "10115", "Strasse 2345",
                "5", "4 UNTERGEScHoß 5"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 5 4 obergeScHoß 5", "Berlin", "10115", "Strasse 2345",
                "5", "4 obergeScHoß 5"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 5 4 obergeScHoss 5", "Berlin", "10115", "Strasse 2345",
                "5", "4 obergeScHoss 5"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 5 4 obergeScHoss y", "Berlin", "10115", "Strasse 2345",
                "5", "4 obergeScHoss y"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 5 4 obergeScHoss 5A", "Berlin", "10115", "Strasse 2345",
                "5", "4 obergeScHoss 5A"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Grub bei St. Anna 73 bei Schmidts", "Berlin", "10115",
                "Grub Bei St. Anna", "73", "bei Schmidts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Grub bei St. Anna 73. bei Schmidts", "Berlin", "10115",
                "Grub Bei St. Anna", "73", "bei Schmidts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Grub bei St. Anna 73, bei Schmidts", "Berlin", "10115",
                "Grub Bei St. Anna", "73", "bei Schmidts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73, bei Ösers", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "bei Ösers"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73, bei Familie Öser", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "bei Familie Öser"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 1 St.", "Berlin", "10115", "Sonnenburgerstr.",
                "73", "1 St."
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 1 Stock", "Berlin", "10115", "Sonnenburgerstr.",
                "73", "1 Stock"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 NEBENGEBÄUDE", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "NEBENGEBÄUDE"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "zinnowitzer str. 1, OG 4", "Berlin", "10115", "Zinnowitzer Str.", "1",
                "OG 4"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 / / !@#$%^&*() // 12   --  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "1=-/A Zinnowitzer Str.", "Berlin", "10115", "Zinnowitzer Str.", "1a",
                null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "1 Zinnowitzer Str.", "Berlin", "10115", "Zinnowitzer Str.", "1", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Frankfurt (Oder) 5", "Berlin", "10115", "Frankfurt (Oder)", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 5", "Berlin", "10115", "Strasse 2345", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "str. 246 - 16a", "Berlin", "10115", "Str. 246", "16a", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "straße 246   16a", "Berlin", "10115", "Straße 246", "16a", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Strasse 2345 , 5", "Berlin", "10115", "Strasse 2345", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Straße 2345 5", "Berlin", "10115", "Straße 2345", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Str. 2345 5", "Berlin", "10115", "Str. 2345", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Str 2345 5", "Berlin", "10115", "Str 2345", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "strasse 2345 5", "Berlin", "10115", "Strasse 2345", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "straße 2345 5", "Berlin", "10115", "Straße 2345", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "str. 2345 5", "Berlin", "10115", "Str. 2345", "5", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "str 2345 5", "Berlin", "10115", "Str 2345", "5", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 10\\00\\1", "Berlin", "10115", "Zinnowitzer Str.",
                "10/1", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1=-/A", "Berlin", "10115", "Zinnowitzer Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1-A", "Berlin", "10115", "Zinnowitzer Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 00//001", "Berlin", "10115", "Zinnowitzer Str.", "1",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1/0/0/00", "Berlin", "10115", "Zinnowitzer Str.",
                "1", null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1", "Berlin", "10115", "Zinnowitzer Str.", "1", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1.", "Berlin", "10115", "Zinnowitzer Str.", "1", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1a", "Berlin", "10115", "Zinnowitzer Str.", "1a", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1A", "Berlin", "10115", "Zinnowitzer Str.", "1a", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 A", "Berlin", "10115", "Zinnowitzer Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1/A", "Berlin", "10115", "Zinnowitzer Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 / A", "Berlin", "10115", "Zinnowitzer Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 / a", "Berlin", "10115", "Zinnowitzer Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str., 1a", "Berlin", "10115", "Zinnowitzer Str.", "1a",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 2 /  3", "Berlin", "10115", "Zinnowitzer Str.",
                "1 2/3", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 2 /  3   A", "Berlin", "10115", "Zinnowitzer Str.",
                "1 2/3a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 2 /  3A", "Berlin", "10115", "Zinnowitzer Str.",
                "1 2/3a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 2 /  3a", "Berlin", "10115", "Zinnowitzer Str.",
                "1 2/3a", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1-12.", "Berlin", "10115", "Zinnowitzer Str.",
                "1-12", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1/12.", "Berlin", "10115", "Zinnowitzer Str.",
                "1/12", null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1/", "Berlin", "10115", "Zinnowitzer Str.", "1", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. /1", "Berlin", "10115", "Zinnowitzer Str.", "1", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. ////1", "Berlin", "10115", "Zinnowitzer Str.", "1",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1//////", "Berlin", "10115", "Zinnowitzer Str.", "1",
                null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 01", "Berlin", "10115", "Zinnowitzer Str.", "1", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 00001", "Berlin", "10115", "Zinnowitzer Str.", "1",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1-----", "Berlin", "10115", "Zinnowitzer Str.", "1",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1&&&&", "Berlin", "10115", "Zinnowitzer Str.", "1",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1%^&$#$%&", "Berlin", "10115", "Zinnowitzer Str.",
                "1", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. %%%1", "Berlin", "10115", "Zinnowitzer Str.", "1",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. ----1----", "Berlin", "10115", "Zinnowitzer Str.",
                "1", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. ----1", "Berlin", "10115", "Zinnowitzer Str.", "1",
                null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 00001///", "Berlin", "10115", "Zinnowitzer Str.",
                "1", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. ///00001///", "Berlin", "10115", "Zinnowitzer Str.",
                "1", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. ///00001", "Berlin", "10115", "Zinnowitzer Str.",
                "1", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. ///00001", "Berlin", "10115", "Zinnowitzer Str.",
                "1", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1/12/14.", "Berlin", "10115", "Zinnowitzer Str.",
                "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 / 12   /  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 //// 12   ///  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 / / // 12   ///  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 / / // 12   --  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 / / &&%// 12   --  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 / / !@#$%^&* // 12   --  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 \\\\ \\  12   \\ --  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 \\\\ \\  12   \\ / /  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 1 -/ 12   /-  14.", "Berlin", "10115",
                "Zinnowitzer Str.", "1/12/14", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Straße des 17. Juni 135", "Berlin", "10115", "Straße Des 17. Juni",
                "135", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Straße des 17 Juni 135", "Berlin", "10115", "Straße Des 17 Juni",
                "135", null
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Straße des 17 Juni 135 A", "Berlin", "10115", "Straße Des 17 Juni",
                "135a", null
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "C8 19", "Berlin", "10115", "C8", "19", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "C8,19", "Berlin", "10115", "C8", "19", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "C8 , 19", "Berlin", "10115", "C8", "19", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "C 8 , 19", "Berlin", "10115", "C 8", "19", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "C 8, 19", "Berlin", "10115", "C 8", "19", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "C8. 19", "Berlin", "10115", "C8.", "19", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "C8 -> 19", "Berlin", "10115", "C8", "19", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "C8 - 19", "Berlin", "10115", "C8", "19", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "Frankfurt (Oder) 5", "Berlin", "10115", "Frankfurt (Oder)", "5", null},
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11 4 OG 5", "Berlin", "10115", "Zinnowitzer Str.",
                "11", "4 OG 5"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11 4 OG. 5", "Berlin", "10115", "Zinnowitzer Str.",
                "11", "4 OG. 5"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11 4 OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4 OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11 / A 4 OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11a", "4 OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11 4 Og .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4 Og .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11 4 og .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4 og .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11, 4 OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4 OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11. 4 OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4 OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11., 4 OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4 OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11., 4OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11., 4. OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4. OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11., OG 4 .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "OG 4 .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11., 4 OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "4 OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73, c/o Zalando, 4. OG", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "c/o Zalando, 4. OG"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 c/o Zalando, 4. OG", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "c/o Zalando, 4. OG"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73, 4. Etage", "Berlin", "10115", "Sonnenburgerstr.",
                "73", "4. Etage"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 4. Etage", "Berlin", "10115", "Sonnenburgerstr.",
                "73", "4. Etage"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 4 Etage", "Berlin", "10115", "Sonnenburgerstr.",
                "73", "4 Etage"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 VH", "Berlin", "10115", "Sonnenburgerstr.", "73",
                "VH"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 VH.", "Berlin", "10115", "Sonnenburgerstr.", "73",
                "VH."
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73, VH.", "Berlin", "10115", "Sonnenburgerstr.",
                "73", "VH."
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73., VH.", "Berlin", "10115", "Sonnenburgerstr.",
                "73", "VH."
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 HH", "Berlin", "10115", "Sonnenburgerstr.", "73",
                "HH"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 HH.", "Berlin", "10115", "Sonnenburgerstr.", "73",
                "HH."
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 hinTerHaus", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "hinTerHaus"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 GH", "Berlin", "10115", "Sonnenburgerstr.", "73",
                "GH"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 gH.", "Berlin", "10115", "Sonnenburgerstr.", "73",
                "gH."
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 GARTenHaus", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "GARTenHaus"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 empfang", "Berlin", "10115", "Sonnenburgerstr.",
                "73", "empfang"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 seitenfluegel", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "seitenfluegel"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 seitenflügel", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "seitenflügel"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 SEITENFLÜGEL", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "SEITENFLÜGEL"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 nebenGebaeude", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "nebenGebaeude"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 nebenGebäudE", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "nebenGebäudE"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Sonnenburgerstr. 73 4 Etage empfang", "Berlin", "10115",
                "Sonnenburgerstr.", "73", "4 Etage empfang"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11. hh 4 OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "hh 4 OG .rechts"
            },
            {
                "Berlin$%^##   ", "1011ABC#$%5", "Zinnowitzer Str. 11. hh4 OG .rechts", "Berlin", "10115",
                "Zinnowitzer Str.", "11", "hh 4 OG .rechts"
            },
            {"Berlin$%^##   ", "1011ABC#$%5", "Alfred-Delp-Straße 2", "Berlin", "10115", "Alfred-Delp-Str.", "2", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "5 Wallstraße 9", "Berlin", "10115", "5 Wallstr.", "9", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "5.Wallstraße 9", "Berlin", "10115", "5.Wallstr.", "9", null},
            {"Berlin$%^##   ", "1011ABC#$%5", "12345", "Berlin", "10115", "", "12345", null},
            {"Berlin$%^##   ", "1011ABC#$%5", null, "Berlin", "10115", "", null, null},
            {"Berlin$%^##   ", "1011ABC#$%5", "", "Berlin", "10115", "", null, null},
            {
                "Berlin", "13129", "Strasse 56 Nr. 10",
                "Berlin", "13129", "Strasse 56", "Nr. 10", null
            },
            {
                "Berlin", "13129", "Strasse 56 10",
                "Berlin", "13129", "Strasse 56", "10", null
            },
            {
                "Dorndorf-Steudnitz", "07774", "Am Rosengarten 14", "Dorndorf-Steudnitz", "07774", "Am Rosengarten",
                "14", null
            },
            {
                "Camburg-Steudnitz ", "07774", "Am Rosengarten 14", "Dorndorf-Steudnitz", "07774", "Am Rosengarten",
                "14", null
            },
            {
                "Dorndorf-Camburg", "07774", "Am Rosengarten 14", "Dorndorf-Steudnitz", "07774", "Am Rosengarten", "14",
                null
            },
            {
                "Camburg-Dorndorf", "07774", "Am Rosengarten 14", "Dorndorf-Steudnitz", "07774", "Am Rosengarten", "14",
                null
            },
            {
                "Berlin", "10115", "Sonnenburgerstr. 73-80",
                "Berlin", "10115", "Sonnenburgerstr.", "73-80", null
            },
            {
                "Berlin", "10115", "Sonnenburgerstr. 73A-80B",
                "Berlin", "10115", "Sonnenburgerstr.", "73a", null
            },
            {
                "Berlin", "13053", "Privatstr. 2 Nr 28",
                "Berlin", "13053", "Privatstr. 2", "Nr. 28", null
            },
            {
                "Berlin", "13053", "Privatstr. 2 Nr. 28",
                "Berlin", "13053", "Privatstr. 2", "Nr. 28", null
            },
            {
                "Berlin", "10101", "Strasse des 17. Juni 2 nr 28",
                "Berlin", "10101", "Strasse Des 17. Juni 2", "Nr. 28", null
            },
        //J+
        };
        return Arrays.asList(data);
    }

    public GermanAddressProcessorGuessTest(final String city, final String zip, final String streetName,
            final String expectedCity, final String expectedZip, final String expectedStreetName,
            final String expectedNr, final String expectedAdditional) {
        this.city = city;
        this.zip = zip;
        this.streetName = streetName;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedName = expectedStreetName;
        this.expectedNr = expectedNr;
        this.expectedAdditional = expectedAdditional;
    }

    @Test
    public void testGuessStreetNumber() throws Exception {

        // for (int i = 0; i < 2500; ++i) {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.DE).city(city).zip(zip)
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
