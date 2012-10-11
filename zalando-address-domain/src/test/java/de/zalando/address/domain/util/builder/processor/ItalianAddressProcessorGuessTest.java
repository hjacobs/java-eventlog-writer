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
public class ItalianAddressProcessorGuessTest {

    private final String city;
    private final String zip;
    private final String streetName;
    private final String additional;
    private final String expectedCity;
    private final String expectedZip;
    private final String expectedName;
    private final String expectedNr;
    private final String expectedAdditional;

    @Parameters
    public static Collection<Object[]> getParameters() {
        final Object[][] data = new Object[][] {

            // @formatter:off
            {"ROME$%^##   ", "00177ABC#$%", "Via Teano, 243", null, "Rome", "00177", "Via Teano", "243", null},
            {"ROCCA PRIORA", "00040", "VIA SAN PAOLO 1", null, "Rocca Priora", "00040", "Via San Paolo", "1", null},
            {
                "OPERA", "20090", "VIA DON GIOVANNI MINZONI 1", null, "Opera", "20090", "Via Don Giovanni Minzoni", "1",
                null
            },
            {"LANUVIO", "00040", "VIA BOEZIO 1", null, "Lanuvio", "00040", "Via Boezio", "1", null},
            {
                "ABBIATEGRASSO", "20081", "VIA BRUNO BUOZZI 1", null, "Abbiategrasso", "20081", "Via Bruno Buozzi", "1",
                null
            },
            {
                "ABBIATEGRASSO", "20081", "VIA DEI MILLE 1/ABC", null, "Abbiategrasso", "20081", "Via Dei Mille",
                "1abc", null
            },
            {
                "ABBIATEGRASSO", "20081", "VIA DEI MILLE 1ABC", null, "Abbiategrasso", "20081", "Via Dei Mille", "1abc",
                null
            },
            {
                "ABBIATEGRASSO", "20081", "VIA DEI MILLE 1A X 5", null, "Abbiategrasso", "20081", "Via Dei Mille", "1a",
                null
            },
            {
                "ABBIATEGRASSO", "20081", "VIA DEI MILLE 1/45", null, "Abbiategrasso", "20081", "Via Dei Mille", "1/45",
                null
            },
            {
                "ABBIATEGRASSO", "20081", "VIA DEI MILLE 1/1A", null, "Abbiategrasso", "20081", "Via Dei Mille", "1/1a",
                null
            },

            // check if this specific case won't be defined differently by
            // team IT (how to handle "1234/2345" or "1324 2345"
            {
                "ABBIATEGRASSO", "20081", "Via Macello 33 83a", null, "Abbiategrasso", "20081", "Via Macello", "33 83a",
                null
            },
            {
                "ABBIATEGRASSO", "20081", "Via Macello 33               83a", null, "Abbiategrasso", "20081",
                "Via Macello", "33 83a", null
            },
            {
                "ABBIATEGRASSO", "20081", "VIA GALILEO FERRARIS 1", null, "Abbiategrasso", "20081",
                "Via Galileo Ferraris", "1", null
            },
            {"VALENZA", "15048", "V BOLOGNA 1", null, "Valenza", "15048", "Via Bologna", "1", null},
            {"VALENZA", "15048", "         V            BOLOGNA 1", null, "Valenza", "15048", "Via Bologna", "1", null},
            {
                "UDINE (Aq.)", "33100", "VIA DA CUSSIGNACCO ARTUICO 1", null, "Udine", "33100",
                "Via Da Cussignacco Artuico", "1", "(Aq.)"
            },
            {
                "UDINE #$$$$$$$#@&#$%$", "33100", "VIA DELL ARTIGIANATO 1", null, "Udine", "33100",
                "Via Dell Artigianato", "1", null
            },
            {
                "UDINE ()", "33100", "VIA CADUTI DEL LAVORO 1", null, "Udine", "33100", "Via Caduti Del Lavoro", "1",
                null
            },
            {"ENNA", "94100", "V. TRAPANI 1", null, "Enna", "94100", "Via Trapani", "1", null},
            {"ENNA", "94100", "VIA MICHELANGELO 1", null, "Enna", "94100", "Via Michelangelo", "1", null},
            {
                "Storo", "38089", "Via Vetiquattro maggio 50", null, "Storo", "38089", "Via Vetiquattro Maggio", "50",
                null
            },
            {
                "Storo", "38089", "PIAZZA VENTISETTE MARZO 1861 50", null, "Storo", "38089",
                "Piazza Ventisette Marzo 1861", "50", null
            },
            {
                "Storo", "38089", "PIAZZA 27 MARZO 1861 50", null, "Storo", "38089", "Piazza Ventisette Marzo 1861",
                "50", null
            },
            {"Storo", "38089", "VIA NOVE GENNAIO 1950 50", null, "Storo", "38089", "Via Nove Gennaio 1950", "50", null},
            {"Storo", "38089", "VIA 9 GENNAIO 1950 50", null, "Storo", "38089", "Via Nove Gennaio 1950", "50", null},

// {"Storo",           "38089",        "Via 1 maggio 50", null,
// "Storo",           "38089",        "Via Uno Maggio",      "50",    null},
            {"Storo", "38089", "Via 2 Santi 50", null, "Storo", "38089", "Via 2 Santi", "50", null},
            {"Storo", "38089", "Via 2 maggio 50", null, "Storo", "38089", "Via Due Maggio", "50", null},
            {"Storo", "38089", "Via 3 maggio 50", null, "Storo", "38089", "Via Tre Maggio", "50", null},
            {"Storo", "38089", "Via 4 maggio 50", null, "Storo", "38089", "Via Quattro Maggio", "50", null},
            {"Storo", "38089", "Via 5 maggio 50", null, "Storo", "38089", "Via Cinque Maggio", "50", null},
            {"Storo", "38089", "Via 6 maggio 50", null, "Storo", "38089", "Via Sei Maggio", "50", null},
            {"Storo", "38089", "Via 7 maggio 50", null, "Storo", "38089", "Via Sette Maggio", "50", null},
            {"Storo", "38089", "Via 8 maggio 50", null, "Storo", "38089", "Via Otto Maggio", "50", null},
            {"Storo", "38089", "Via 9 maggio 50", null, "Storo", "38089", "Via Nove Maggio", "50", null},
            {"Storo", "38089", "Via 10 maggio 50", null, "Storo", "38089", "Via Dieci Maggio", "50", null},
            {"Storo", "38089", "Via 11 maggio 50", null, "Storo", "38089", "Via Undici Maggio", "50", null},
            {"Storo", "38089", "Via 12 maggio 50", null, "Storo", "38089", "Via Dodici Maggio", "50", null},
            {"Storo", "38089", "Via 13 maggio 50", null, "Storo", "38089", "Via Tredici Maggio", "50", null},
            {"Storo", "38089", "Via 14 maggio 50", null, "Storo", "38089", "Via Quattordici Maggio", "50", null},
            {"Storo", "38089", "Via 15 maggio 50", null, "Storo", "38089", "Via Quindici Maggio", "50", null},
            {"Storo", "38089", "Via 16 maggio 50", null, "Storo", "38089", "Via Sedici Maggio", "50", null},
            {"Storo", "38089", "Via 17 maggio 50", null, "Storo", "38089", "Via Diciasette Maggio", "50", null},
            {"Storo", "38089", "Via 18 maggio 50", null, "Storo", "38089", "Via Diciotto Maggio", "50", null},
            {"Storo", "38089", "Via 19 maggio 50", null, "Storo", "38089", "Via Diciannove Maggio", "50", null},
            {"Storo", "38089", "Via 20 maggio 50", null, "Storo", "38089", "Via Venti Maggio", "50", null},
            {"Storo", "38089", "Via 21 maggio 50", null, "Storo", "38089", "Via Ventuno Maggio", "50", null},
            {"Storo", "38089", "Via 22 maggio 50", null, "Storo", "38089", "Via Ventidue Maggio", "50", null},
            {"Storo", "38089", "Via 23 maggio 50", null, "Storo", "38089", "Via Ventitré Maggio", "50", null},
            {"Storo", "38089", "Via 24 maggio 50", null, "Storo", "38089", "Via Ventiquattro Maggio", "50", null},
            {"Storo", "38089", "Via 25 maggio 50", null, "Storo", "38089", "Via Venticinque Maggio", "50", null},
            {"Storo", "38089", "Via 26 maggio 50", null, "Storo", "38089", "Via Ventisei Maggio", "50", null},
            {"Storo", "38089", "Via 27 maggio 50", null, "Storo", "38089", "Via Ventisette Maggio", "50", null},
            {"Storo", "38089", "Via 28 maggio 50", null, "Storo", "38089", "Via Ventotto Maggio", "50", null},
            {"Storo", "38089", "Via 29 maggio 50", null, "Storo", "38089", "Via Ventinove Maggio", "50", null},
            {"Storo", "38089", "Via 30 maggio 50", null, "Storo", "38089", "Via Trenta Maggio", "50", null},
            {
                "Storo", "38089", "Via 30              maggio 50", null, "Storo", "38089", "Via Trenta Maggio", "50",
                null
            },
            {"Pescara", "65124", "Strada Prati 2/1", null, "Pescara", "65124", "Strada Prati", "2/1", null},
            {
                "Velletri", "00049", "Piazza XX Settembre 7", null, "Velletri", "00049", "Piazza Venti Settembre", "7",
                null
            },
            {"Noceto (Pr)", "43015", "Via Xxv Aprile 7", null, "Noceto", "43015", "Via XXV Aprile", "7", "(Pr)"},
            {"Pozzuoli (Na)", "80078", "Via Antiniana 2a", null, "Pozzuoli", "80078", "Via Antiniana", "2a", "(Na)"},
            {
                "San.Miniato ( Fraz.S.Donato ) Pisa", "56024", "S.Donato 17/21", null, "San.Miniato  Pisa", "56024",
                "S.Donato", "17/21", "( Fraz.S.Donato )"
            },
            {"Broni (Pv)", "27043", "Monte Grappa 45/47", null, "Broni", "27043", "Monte Grappa", "45/47", "(Pv)"},
            {"Rovereto (Tn)", "38068", "Via Fornaci 8", null, "Rovereto", "38068", "Via Fornaci", "8", "(Tn)"},
            {
                "Portopalo Di C.P. (Sr)", "96010", "Via Lucio Tasca 71", null, "Portopalo Di C.P.", "96010",
                "Via Lucio Tasca", "71", "(Sr)"
            },
            {
                "Riva Del Garda (Tn)", "38066", "Via Virgilio 11", null, "Riva Del Garda", "38066", "Via Virgilio",
                "11", "(Tn)"
            },
            {
                "San Vincenzo (Li)", "57027", "Via Per Campiglia 7", null, "San Vincenzo", "57027", "Via Per Campiglia",
                "7", "(Li)"
            },
            {
                "San Vincenzo (Li)", "57027", "Via Federigo Tognarini 150", null, "San Vincenzo", "57027",
                "Via Federigo Tognarini", "150", "(Li)"
            },
            {
                "Pontedera (Pi)", "56025", "Via Dei Fabbri 22", null, "Pontedera", "56025", "Via Dei Fabbri", "22",
                "(Pi)"
            },
            {
                "Prevalle (Bs)", "25080", "Via 11 Febbraio 34", null, "Prevalle", "25080", "Via Undici Febbraio", "34",
                "(Bs)"
            },
            {"Rovereto (Tn)", "38068", "Per Marco 16", null, "Rovereto", "38068", "Per Marco", "16", "(Tn)"},
            {
                "Fonte Nuova (Rm)", "00013", "Via Tor Sant Antonio 34", null, "Fonte Nuova", "00013",
                "Via Tor Sant Antonio", "34", "(Rm)"
            },
            {
                "Basiglio (Mi)", "20080", "Via Colombo Residenza Fontanile 611", null, "Basiglio", "20080",
                "Via Colombo Residenza Fontanile", "611", "(Mi)"
            },
            {
                "Cossila S. Grato ( Bi )", "13892", "Strada Bufarola 14", null, "Cossila S. Grato", "13892",
                "Strada Bufarola", "14", "( Bi )"
            },
            {
                "Roveredo In Piano (Pn)", "33080", "Via Pionieri Dell Aria 51", null, "Roveredo In Piano", "33080",
                "Via Pionieri Dell Aria", "51", "(Pn)"
            },
            {"Orte (Vt)", "01028", "Via Raffaello Snc", null, "Orte", "01028", "Via Raffaello", null, "(Vt)"},
            {"Orte (Vt)", "01028", "Via Raffaello (Snc)", null, "Orte", "01028", "Via Raffaello", null, "(Vt)"},
            {
                "Porto Viro (Ro)", "45014", "Borgo Biancospino 5", null, "Porto Viro", "45014", "Borgo Biancospino",
                "5", "(Ro)"
            },
            {
                "San Cipriano Picentino (Sa)", "84099", "Via Vetrale 4", null, "San Cipriano Picentino", "84099",
                "Via Vetrale", "4", "(Sa)"
            },
            {
                "Reggello (Fi)", "50066", "Via G.Ungaretti 14", null, "Reggello", "50066", "Via G.Ungaretti", "14",
                "(Fi)"
            },
            {"Castelli (Te)", "64041", "Via Faiano 20", null, "Castelli", "64041", "Via Faiano", "20", "(Te)"},
            {"Carpi (Modena)", "41012", "Via Verdi 9", null, "Carpi", "41012", "Via Verdi", "9", "(Modena)"},
            {"Loc. Soci (Ar)", "52010", "Via Del Prato 16", null, "Loc. Soci", "52010", "Via Del Prato", "16", "(Ar)"},
            {"Orino (Va)", "21030", "Vicolo Moia 2", null, "Orino", "21030", "Vicolo Moia", "2", "(Va)"},
            {
                "Sant Ilario Dello Ionio (Rc)", "89040", "San Martino N 2", null, "Sant Ilario Dello Ionio", "89040",
                "San Martino", "2", "(Rc)"
            },
            {
                "Granarolo Dell Emilia (Bo)", "40057", "Carducci 7", null, "Granarolo Dell Emilia", "40057", "Carducci",
                "7", "(Bo)"
            },
            {"La Villa (Bz)", "39030", "Bosc Da Plan 43", null, "La Villa", "39030", "Bosc Da Plan", "43", "(Bz)"},
            {
                "Roccapiemonte ( Salerno )", "84086", "Via Della Pace N 56", null, "Roccapiemonte", "84086",
                "Via Della Pace", "56", "( Salerno )"
            },
            {"Nettuno (Rm)", "00048", "Via Abano 4", null, "Nettuno", "00048", "Via Abano", "4", "(Rm)"},
            {"Sindia (Nu)", "08018", "Via Santa Daria 12", null, "Sindia", "08018", "Via Santa Daria", "12", "(Nu)"},
            {
                "Vezzano Ligure (Sp)", "19020", "Via Borrotzu 33b", null, "Vezzano Ligure", "19020", "Via Borrotzu",
                "33b", "(Sp)"
            },
            {
                "Atripalda (Av)", "83042", "Via Appia Settima Traversa N. 34", null, "Atripalda", "83042",
                "Via Appia Settima Traversa", "34", "(Av)"
            },
            {
                "Vinchiaturo (Cb)", "86019", "Corso Del Popolo Snc", null, "Vinchiaturo", "86019", "Corso Del Popolo",
                null, "(Cb)"
            },
            {
                "Rovereto (Frazione Marco)", "38068", "Via Lunga 44", null, "Rovereto", "38068", "Via Lunga", "44",
                "(Frazione Marco)"
            },
            {
                "San Severino Marche (Mc)", "62027", "Via Garibaldi 6", null, "San Severino Marche", "62027",
                "Via Garibaldi", "6", "(Mc)"
            },
            {
                "Gubbio (Pg)", "06024", "Via Luigi Bellucci 18", null, "Gubbio", "06024", "Via Luigi Bellucci", "18",
                "(Pg)"
            },
            {"Bressanone (Bz)", "39042", "Via Artmanno 17", null, "Bressanone", "39042", "Via Artmanno", "17", "(Bz)"},
            {
                "Pezzan Di Carbonera (Tv)", "31030", "Via Iv Novembre 115", null, "Pezzan Di Carbonera", "31030",
                "Via Quattro Novembre", "115", "(Tv)"
            },
            {
                "Guagnano (Lecce)", "73010", "Via Amerigo Vespucci N. 19", null, "Guagnano", "73010",
                "Via Amerigo Vespucci", "19", "(Lecce)"
            },
            {
                "Cornate D Adda", "20040", "Via Primo Stucchi 17", null, "Cornate D Adda", "20040", "Via Primo Stucchi",
                "17", null
            },
            {
                "Cornate D Adda (Mb)", "20040", "Via Primo Stucchi 17", null, "Cornate D Adda", "20040",
                "Via Primo Stucchi", "17", "(Mb)"
            },
            {
                "Cornate D Adda (Mb)", "20040", "Via Della Repubblica 8", null, "Cornate D Adda", "20040",
                "Via Della Repubblica", "8", "(Mb)"
            },
            {
                "Cornate D Adda (Mb)", "20040", "Via Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Via Della Repubblica", "8", "(Mb)"
            },
            {
                "Cornate D Adda, Mb", "20040", "Via Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Via Della Repubblica", "8", "Mb"
            },
            {
                "Cornate D Adda, (Mb)", "20040", "Via Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Via Della Repubblica", "8", "(Mb)"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "Via Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Via Della Repubblica", "8", "Mezzano"
            },
            {"Broni (Pv)", "27043", "Monte Grappa 45/47", "xyz", "Broni", "27043", "Monte Grappa", "45/47", "xyz (Pv)"},
            {"Broni", "27043", "Monte Grappa 45/47", "xyz", "Broni", "27043", "Monte Grappa", "45/47", "xyz"},
            {
                "Velletri", "00049", "Piazza V Settembre XX 7", null, "Velletri", "00049", "Piazza Cinque Settembre XX",
                "7", null
            },
            {"Roma, Apolda Weimar", "00049", "Viale Marx 1", null, "Roma", "00049", "Viale Marx", "1", "Apolda Weimar"},
            {
                "D Adda, Mezzano", "20040", "P.zza Repubblica nr 8", null, "D Adda", "20040", "Piazza Repubblica", "8",
                "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "P.za Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Piazza Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "C.sO Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Corso Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "V.lo Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Vicolo Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "Piazz.le Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Piazzale Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "p.le Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Piazzale Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "Piaz.le Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Piazzale Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "reg Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Regione Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "c.da Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Contrada Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "gall Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Galleria Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "l.arno Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Lungarno Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "b.go Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Borgo Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "cor Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Corso Della Repubblica", "8", "Mezzano"
            },
            {
                "Cornate D Adda, Mezzano", "20040", "pi.le  Della Repubblica nr 8", null, "Cornate D Adda", "20040",
                "Piazzale Della Repubblica", "8", "Mezzano"
            },

            {
                "L'Aquila, Italia", "67100", "Strada Statale 17", null, "L'Aquila", "67100", "Strada Statale", "17",
                "Italia"
            },

            // @formatter:on
        };
        return Arrays.asList(data);
    }

    public ItalianAddressProcessorGuessTest(final String city, final String zip, final String streetName,
            final String additional, final String expectedCity, final String expectedZip, final String expectedName,
            final String expectedNr, final String expectedAdditional) {
        this.city = city;
        this.zip = zip;
        this.streetName = streetName;
        this.additional = additional;
        this.expectedCity = expectedCity;
        this.expectedZip = expectedZip;
        this.expectedName = expectedName;
        this.expectedNr = expectedNr;
        this.expectedAdditional = expectedAdditional;
    }

    @Test
    public void testGuessStreetNumber() throws Exception {

        // for (int i = 0; i < 2500; ++i) {
        final Address address = AddressBuilder.forCountry(ISOCountryCode.IT).city(city).zip(zip)
                                              .streetAddition(additional).streetWithHouseNumber(streetName).build();
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
