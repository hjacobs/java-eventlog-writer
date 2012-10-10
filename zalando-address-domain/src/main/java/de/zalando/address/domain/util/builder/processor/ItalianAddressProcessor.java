package de.zalando.address.domain.util.builder.processor;

import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compilePatterns;
import static de.zalando.address.domain.util.builder.AddressProcessorUtil.compileReplacePattern;

import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;

import de.zalando.address.domain.util.builder.NumberPosition;

import de.zalando.utils.Pair;

public class ItalianAddressProcessor extends AbstractAddressProcessor {

    // This is what we need to collect.
    private static final String NR_REGEX = "((?:\\d+[\\p{Punct}\\s]+)*\\d+[\\p{Punct}\\s]*[a-zA-Z]*\\b)";

    private static final String MONTHS_REGEX = "(?:gennaio|febbraio|marzo|maggio|"
            + "giugnu|luglio|agosto|settembre|ottobre|novembre|dicembre)";

    // The order of the regexen is IMPORTANT!
    private static final String[] NUMBER_REGEXEN = new String[] {
        // @formatter:off

        MONTHS_REGEX + "\\s+[0-9]{4}\\s+" + NR_REGEX, "\\d+\\s?[.,]?\\s?[\\p{L}.]+\\s" + NR_REGEX,
        "\\d+\\s?[.,]+\\s?" + NR_REGEX, "^[a-zA-Z]\\s?\\d\\s?(?:\\D\\s?)?" + NR_REGEX, "\\d\\s?[,.]+\\s?" + NR_REGEX,
        NR_REGEX, "([0-9]{1,5} ?[a-zA-Z]{0,1})"
        // @formatter:on
    };

    private static final ImmutableList<Pattern> NUMBER_PATTERNS;

    private static final String[] ADDITIONAL_REGEXEN = new String[0];
    private static final String[] CITY_ADDITIONAL_REGEXEN = new String[] { // @formatter:off
        "\\s*(\\(.+\\))", ",\\s*(.+)"
        // @formatter:on
    };

    private static final ImmutableList<Pattern> ADDITIONAL_PATTERNS;
    private static final ImmutableList<Pattern> CITY_ADDITIONAL_PATTERNS;

    private static final String[][] NUMBER_REPLACE_REGEXEN = new String[][] {

        // @formatter:off
        {"\\b0+\\b", ""},
        {"[^-/\\d\\p{L}.]+", " "},
        {"\\b0+(.*)", "$1"},
        {" ?/+ ?", "/"},
        {" ?-+ ?", "-"},
        {" ?\\.+ ?", "."},
        {"[-/.]{2,}", "/"},
        {"^[-/.]+(\\d.*)", "$1"},
        {"(\\d+)[-/ ]+(\\D+)", "$1$2"},
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> NUMBER_REPLACE_PATTERNS;

    // @formatter:off
    private static final String[][] STREET_REPLACE_REGEXEN = new String[][] {
        {"\\(?snc\\)?", ""},
        {"\\bNR?\\b\\s*\\p{Punct}*", ""},

        // numbers
        {"\\b2\\b\\s+(" + MONTHS_REGEX + ')', "DUE $1"},
        {"\\b3\\b\\s+(" + MONTHS_REGEX + ')', "TRE $1"},
        {"\\b4\\b\\s+(" + MONTHS_REGEX + ')', "QUATTRO $1"},
        {"\\b5\\b\\s+(" + MONTHS_REGEX + ')', "CINQUE $1"},
        {"\\b6\\b\\s+(" + MONTHS_REGEX + ')', "SEI $1"},
        {"\\b7\\b\\s+(" + MONTHS_REGEX + ')', "SETTE $1"},
        {"\\b8\\b\\s+(" + MONTHS_REGEX + ')', "OTTO $1"},
        {"\\b9\\b\\s+(" + MONTHS_REGEX + ')', "NOVE $1"},
        {"\\b10\\b\\s+(" + MONTHS_REGEX + ')', "DIECI $1"},
        {"\\b11\\b\\s+(" + MONTHS_REGEX + ')', "UNDICI $1"},
        {"\\b12\\b\\s+(" + MONTHS_REGEX + ')', "DODICI $1"},
        {"\\b13\\b\\s+(" + MONTHS_REGEX + ')', "TREDICI $1"},
        {"\\b14\\b\\s+(" + MONTHS_REGEX + ')', "QUATTORDICI $1"},
        {"\\b15\\b\\s+(" + MONTHS_REGEX + ')', "QUINDICI $1"},
        {"\\b16\\b\\s+(" + MONTHS_REGEX + ')', "SEDICI $1"},
        {"\\b17\\b\\s+(" + MONTHS_REGEX + ')', "DICIASETTE $1"},
        {"\\b18\\b\\s+(" + MONTHS_REGEX + ')', "DICIOTTO $1"},
        {"\\b19\\b\\s+(" + MONTHS_REGEX + ')', "DICIANNOVE $1"},
        {"\\b20\\b\\s+(" + MONTHS_REGEX + ')', "VENTI $1"},
        {"\\b21\\b\\s+(" + MONTHS_REGEX + ')', "VENTUNO $1"},
        {"\\b22\\b\\s+(" + MONTHS_REGEX + ')', "VENTIDUE $1"},
        {"\\b23\\b\\s+(" + MONTHS_REGEX + ')', "VENTITRÉ $1"},
        {"\\b24\\b\\s+(" + MONTHS_REGEX + ')', "VENTIQUATTRO $1"},
        {"\\b25\\b\\s+(" + MONTHS_REGEX + ')', "VENTICINQUE $1"},
        {"\\b26\\b\\s+(" + MONTHS_REGEX + ')', "VENTISEI $1"},
        {"\\b27\\b\\s+(" + MONTHS_REGEX + ')', "VENTISETTE $1"},
        {"\\b28\\b\\s+(" + MONTHS_REGEX + ')', "VENTOTTO $1"},
        {"\\b29\\b\\s+(" + MONTHS_REGEX + ')', "VENTINOVE $1"},
        {"\\b30\\b\\s+(" + MONTHS_REGEX + ')', "TRENTA $1"},
        {"\\bII\\b\\s+(" + MONTHS_REGEX + ')', "DUE $1"},
        {"\\bIII\\b\\s+(" + MONTHS_REGEX + ')', "TRE $1"},
        {"\\bIV\\b\\s+(" + MONTHS_REGEX + ')', "QUATTRO $1"},
        {"\\bV\\b\\s+(" + MONTHS_REGEX + ')', "CINQUE $1"},
        {"\\bVI\\b\\s+(" + MONTHS_REGEX + ')', "SEI $1"},
        {"\\bVII\\b\\s+(" + MONTHS_REGEX + ')', "SETTE $1"},
        {"\\bVIII\\b\\s+(" + MONTHS_REGEX + ')', "OTTO $1"},
        {"\\bIX\\b\\s+(" + MONTHS_REGEX + ')', "NOVE $1"},
        {"\\bX\\b\\s+(" + MONTHS_REGEX + ')', "DIECI $1"},
        {"\\bXI\\b\\s+(" + MONTHS_REGEX + ')', "UNDICI $1"},
        {"\\bXII\\b\\s+(" + MONTHS_REGEX + ')', "DODICI $1"},
        {"\\bXIII\\b\\s+(" + MONTHS_REGEX + ')', "TREDICI $1"},
        {"\\bXIV\\b\\s+(" + MONTHS_REGEX + ')', "QUATTORDICI $1"},
        {"\\bXV\\b\\s+(" + MONTHS_REGEX + ')', "QUINDICI $1"},
        {"\\bXVI\\b\\s+(" + MONTHS_REGEX + ')', "SEDICI $1"},
        {"\\bXVII\\b\\s+(" + MONTHS_REGEX + ')', "DICIASETTE $1"},
        {"\\bXVIII\\b\\s+(" + MONTHS_REGEX + ')', "DICIOTTO $1"},
        {"\\bXIX\\b\\s+(" + MONTHS_REGEX + ')', "DICIANNOVE $1"},
        {"\\bXX\\b\\s+(" + MONTHS_REGEX + ')', "VENTI $1"},
        {"\\bXXI\\b\\s+(" + MONTHS_REGEX + ')', "VENTUNO $1"},
        {"\\bXXII\\b\\s+(" + MONTHS_REGEX + ')', "VENTIDUE $1"},
        {"\\bXXIII\\b\\s+(" + MONTHS_REGEX + ')', "VENTITRÉ $1"},
        {"\\bXXIV\\b\\s+(" + MONTHS_REGEX + ')', "VENTIQUATTRO $1"},
        {"\\bXXV\\b\\s+(" + MONTHS_REGEX + ')', "VENTICINQUE $1"},
        {"\\bXXVI\\b\\s+(" + MONTHS_REGEX + ')', "VENTISEI $1"},
        {"\\bXXVII\\b\\s+(" + MONTHS_REGEX + ')', "VENTISETTE $1"},
        {"\\bXXVIII\\b\\s+(" + MONTHS_REGEX + ')', "VENTOTTO $1"},
        {"\\bXXIX\\b\\s+(" + MONTHS_REGEX + ')', "VENTINOVE $1"},
        {"\\bXXX\\b\\s+(" + MONTHS_REGEX + ')', "TRENTA $1"},

        // DUG Abbreviations not in AbbreviazioniDug.txt
        {"\\bP\\.ZZA\\b\\.*", "PIAZZA"},
        {"\\bP\\.ZA\\b\\.*", "PIAZZA"},
        {"\\bC\\.SO\\b\\.*", "CORSO"},
        {"\\bV\\.LO\\b\\.*", "VICOLO"},
        {"\\bPIAZZ\\.LE\\b\\.*", "PIAZZALE"},
        {"\\bP\\.LE\\b\\.*", "PIAZZALE"},
        {"\\bPIAZ\\.LE\\b\\.*", "PIAZZALE"},
        {"\\bREG\\b\\.*", "REGIONE"},
        {"\\bC\\.DA\\b\\.*", "CONTRADA"},
        {"\\bL\\.GO\\b\\.*", "LARGO"},
        {"\\bGALL\\b\\.*", "GALLERIA"},
        {"\\bL\\.ARNO\\b\\.*", "LUNGARNO"},
        {"\\bB\\.GO\\b\\.*", "BORGO"},
        {"\\bCOR\\b\\.*", "CORSO"},
        {"\\bPI\\.LE\\b\\.*", "PIAZZALE"},

        // DUG Abbreviations (subset of AbbreviazioniDug.txt)
        {"\\bASTR\\b\\.*", "AUTOSTRADA"},
        {"\\bBAGL\\b\\.*", "BAGLIO"},
        {"\\bBVIO\\b\\.*", "BIVIO"},
        {"\\bCALA\\b\\.*", "CALATA"},
        {"\\bCALO\\b\\.*", "CALLE LONGA"},
        {"\\bCASP\\b\\.*", "CASE SPARSE"},
        {"\\bCHLO\\b\\.*", "CHIASSUOLO"},
        {"\\bCHSA\\b\\.*", "CHIESA"},
        {"\\bCIRC\\b\\.*", "CIRCONVALLAZIONE"},
        {"\\bCLLA\\b\\.*", "CALLE LARGA"},
        {"\\bCLLE\\b\\.*", "CALLE"},
        {"\\bCMPL\\b\\.*", "CAMPIELLO"},
        {"\\bCMPO\\b\\.*", "CAMPO"},
        {"\\bCMPR\\b\\.*", "COMPRENSORIO"},
        {"\\bCOMR\\b\\.*", "COMPLESSO RESIDENZIALE"},
        {"\\bCONT\\b\\.*", "CONTRADA"},
        {"\\bCORT\\b\\.*", "CORTILE"},
        {"\\bCRCS\\b\\.*", "CIRCONVALLAZIONE STATALE"},
        {"\\bCRRD\\b\\.*", "CARRIZZADA"},
        {"\\bCRTE\\b\\.*", "CORTE"},
        {"\\bCSO\\b\\.*", "CORSO"},
        {"\\bCUPA\\b\\.*", "CUPA VICINALE"},
        {"\\bCVAL\\b\\.*", "CAVALCAVIA"},
        {"\\bDISC\\b\\.*", "DISCESA"},
        {"\\bDRCO\\b\\.*", "DIRAMAZIONE COSTANTINO"},
        {"\\bDRGU\\b\\.*", "DIRAMAZIONE GULLI"},
        {"\\bDRMN\\b\\.*", "DIRAMAZIONE"},
        {"\\bDRPR\\b\\.*", "DIRAMAZIONE I"},
        {"\\bDRPT\\b\\.*", "DIRAMAZIONE PRIVATA"},
        {"\\bDRSE\\b\\.*", "DIRAMAZIONE II"},
        {"\\bDRTR\\b\\.*", "DIRAMAZIONE III"},
        {"\\bFNDA\\b\\.*", "FONDAMENTA"},
        {"\\bFRAZ\\b\\.*", "FRAZIONE"},
        {"\\bGRAN\\b\\.*", "GRAN VIALE"},
        {"\\bGRNI\\b\\.*", "GIARDINI"},
        {"\\bGRNO\\b\\.*", "GIARDINO"},
        {"\\bGRPM\\b\\.*", "GRUPPO A MONTE"},
        {"\\bGRPV\\b\\.*", "GRUPPO A VALLE"},
        {"\\bLCAN\\b\\.*", "LUNGO CANALE"},
        {"\\bLGO\\b\\.*", "LARGO"},
        {"\\bLOC\\b\\.*", "LOCALITA"},
        {"\\bLRTT\\b\\.*", "LARGHETTO"},
        {"\\bLTEV\\b\\.*", "LUNGOTEVERE"},
        {"\\bLTOR\\b\\.*", "LITORANEA"},
        {"\\bPASS\\b\\.*", "PASSAGGIO"},
        {"\\bPLTR\\b\\.*", "PARALLELA III"},
        {"\\bPODE\\b\\.*", "PODERE"},
        {"\\bPRLL\\b\\.*", "PARALLELA"},
        {"\\bPROL\\b\\.*", "PROLUNGAMENTO"},
        {"\\bPRTT\\b\\.*", "PORTICHETTI"},
        {"\\bPSGP\\b\\.*", "PASSAGGIO PRIVATO"},
        {"\\bPSO\\b\\.*", "PASSO"},
        {"\\bPTE\\b\\.*", "PONTE"},
        {"\\bPTTA\\b\\.*", "PIAZZETTA"},
        {"\\bPZLE\\b\\.*", "PIAZZALE"},
        {"\\bPZTA\\b\\.*", "PIAZZA PRIVATA"},
        {"\\bPZZA\\b\\.*", "PIAZZA"},
        {"\\bQUAR\\b\\.*", "QUARTIERE"},
        {"\\bRACC\\b\\.*", "RACCORDO"},
        {"\\bRCAU\\b\\.*", "RACCORDO AUTOSTRADALE"},
        {"\\bREGN\\b\\.*", "REGIONE"},
        {"\\bRIOT\\b\\.*", "RIO TERRA"},
        {"\\bRITE\\b\\.*", "RIO TERA"},
        {"\\bRTND\\b\\.*", "ROTONDA"},
        {"\\bSAIN\\b\\.*", "SALITA INFERIORE"},
        {"\\bSAL\\b\\.*", "SALITA"},
        {"\\bSALS\\b\\.*", "SALITA SUPERIORE"},
        {"\\bSAZD\\b\\.*", "SALIZADA"},
        {"\\bSCTA\\b\\.*", "SCALINATA"},
        {"\\bSGIO\\b\\.*", "SOTTOPASSAGGIO"},
        {"\\bSOTP\\b\\.*", "SOTOPORTEGO"},
        {"\\bSP\\b\\.*", "STRADA PROVINCIALE"},
        {"\\bSPSE\\b\\.*", "SOTTOPORTICO II"},
        {"\\bSS\\b\\.*", "STRADA STATALE"},
        {"\\bSTAR\\b\\.*", "STRADA ARGINALE"},
        {"\\bSTAT\\b\\.*", "STATALE"},
        {"\\bSTAZ\\b\\.*", "STAZIONE"},
        {"\\bSTCA\\b\\.*", "STRADA COMUNALE ANTICA"},
        {"\\bSTCO\\b\\.*", "STRADA COMUNALE"},
        {"\\bSTES\\b\\.*", "STRADA ESTERNA"},
        {"\\bSTLL\\b\\.*", "STRADELLO"},
        {"\\bSTNZ\\b\\.*", "STRADA NAZIONALE"},
        {"\\bSTPC\\b\\.*", "SOTTOPORTICO"},
        {"\\bSTPV\\b\\.*", "STRADA PRIVATA"},
        {"\\bSTR\\b\\.*", "STRADA"},
        {"\\bSTVC\\b\\.*", "STRADA VICINALE CUPA"},
        {"\\bSTVN\\b\\.*", "STRADA VICINALE"},
        {"\\bSUPP\\b\\.*", "SUPPORTICO"},
        {"\\bSVIN\\b\\.*", "SVINCOLO"},
        {"\\bTPRD\\b\\.*", "TRAVERSA I DESTRA"},
        {"\\bTPRS\\b\\.*", "TRAVERSA I SINISTRA"},
        {"\\bTQTD\\b\\.*", "TRAVERSA V DESTRA"},
        {"\\bTQTS\\b\\.*", "TRAVERSA V SINISTRA"},
        {"\\bTQUD\\b\\.*", "TRAVERSA IV DESTRA"},
        {"\\bTQUS\\b\\.*", "TRAVERSA IV SINISTRA"},
        {"\\bTRAV\\b\\.*", "TRAVERSA"},
        {"\\bTRDB\\b\\.*", "TRAVERSA DE BLASIO"},
        {"\\bTRLA\\b\\.*", "TRAVERSA LABATE"},
        {"\\bTRPV\\b\\.*", "TRAVERSA PRIVATA"},
        {"\\bTRSN\\b\\.*", "TRAVERSA SINISTRA"},
        {"\\bTRSS\\b\\.*", "TRAVERSA VI"},
        {"\\bTSED\\b\\.*", "TRAVERSA II DESTRA"},
        {"\\bTSEP\\b\\.*", "TRAVERSA II PRIVATA"},
        {"\\bTSES\\b\\.*", "TRAVERSA II SINISTRA"},
        {"\\bTSTS\\b\\.*", "TRAVERSA VII SINISTRA"},
        {"\\bTTRD\\b\\.*", "TRAVERSA III DESTRA"},
        {"\\bTTRS\\b\\.*", "TRAVERSA III SINISTRA"},
        {"\\bTVOT\\b\\.*", "TRAVERSA VIII"},
        {"\\bTVPR\\b\\.*", "TRAVERSA I"},
        {"\\bTVQT\\b\\.*", "TRAVERSA V"},
        {"\\bTVQU\\b\\.*", "TRAVERSA IV"},
        {"\\bTVSE\\b\\.*", "TRAVERSA II"},
        {"\\bTVTR\\b\\.*", "TRAVERSA III"},
        {"\\bV\\b\\.*", "VIA"},
        {"\\bVANE\\b\\.*", "VALLONE"},
        {"\\bVANT\\b\\.*", "VIA ANTICA"},
        {"\\bVCLO\\b\\.*", "VICOLO"},
        {"\\bVCLU\\b\\.*", "VICO LUNGO"},
        {"\\bVCML\\b\\.*", "VIA COMUNALE"},
        {"\\bVCPR\\b\\.*", "VICO I"},
        {"\\bVCSE\\b\\.*", "VICO II"},
        {"\\bVCTR\\b\\.*", "VICO III"},
        {"\\bVICL\\b\\.*", "VICINALE"},
        {"\\bVILL\\b\\.*", "VILLAGGIO"},
        {"\\bVINT\\b\\.*", "VIA INTERMEDIA"},
        {"\\bVLCI\\b\\.*", "VICOLO CIECO"},
        {"\\bVLE\\b\\.*", "VIALE"},
        {"\\bVLPV\\b\\.*", "VIALE PRIVATO"},
        {"\\bVNVA\\b\\.*", "VIA NUOVA"},
        {"\\bVOC\\b\\.*", "VOCABOLO"},
        {"\\bVPAN\\b\\.*", "VIA PANORAMICA"},
        {"\\bVPRO\\b\\.*", "VIA PROVINCIALE"},
        {"\\bVPRV\\b\\.*", "VIA PRIVATA"},
        {"\\bVSTO\\b\\.*", "VICO STORTO"},
        {"\\bVTCI\\b\\.*", "VICOLETTO CIECO"},
        {"\\bVTTO\\b\\.*", "VICOLETTO"},
        {"\\bVTTR\\b\\.*", "VICOLETTO III"},
        {"\\bVVCM\\b\\.*", "VIA VECCHIA COMUNALE"},
        {"\\bVVEC\\b\\.*", "VIA VECCHIA"},
        {"\\bVVIC\\b\\.*", "VIA VICINALE"},
    };
    // @formatter:on
    // {"\\b1\\b (" + MONTHS_REGEXEN + ")",
    // "UNO $1"},

    private static final ImmutableList<Pair<Pattern, String>> STREET_REPLACE_PATTERNS;

    private static final String[][] COMPLETE_STREET_REPLACE_REGEXEN = new String[][] {
// @formatter:off
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> COMPLETE_STREET_REPLACE_PATTERNS;

    private static final String[][] CITY_REPLACE_REGEXEN = new String[][] {
// @formatter:off
        {"\\b(\\s*,.*)", ""},
        {"\\(\\W*\\)", ""}
        // @formatter:on
    };

    private static final ImmutableList<Pair<Pattern, String>> CITY_REPLACE_PATTERNS;

    private static final Pattern UNALLOWED_CHARACTERS_PATTERN = Pattern.compile("[^-(),\\p{L}\\d./']");

    private static final char[] CAP_DELIMITERS = new char[] {'(', '-', ' ', '[', '.', '\''};

    static {
        NUMBER_PATTERNS = compilePatterns(NUMBER_REGEXEN, Pattern.CASE_INSENSITIVE);
        ADDITIONAL_PATTERNS = compilePatterns(ADDITIONAL_REGEXEN, Pattern.CASE_INSENSITIVE);
        NUMBER_REPLACE_PATTERNS = compileReplacePattern(NUMBER_REPLACE_REGEXEN);
        STREET_REPLACE_PATTERNS = compileReplacePattern(STREET_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);
        COMPLETE_STREET_REPLACE_PATTERNS = compileReplacePattern(COMPLETE_STREET_REPLACE_REGEXEN,
                Pattern.CASE_INSENSITIVE);
        CITY_REPLACE_PATTERNS = compileReplacePattern(CITY_REPLACE_REGEXEN, Pattern.CASE_INSENSITIVE);
        CITY_ADDITIONAL_PATTERNS = compilePatterns(CITY_ADDITIONAL_REGEXEN, Pattern.CASE_INSENSITIVE);
    }

    @Override
    protected char[] getCapitalizationDelimiters() {
        return CAP_DELIMITERS;
    }

    @Override
    public NumberPosition getNumberPosition() {
        return NumberPosition.RIGHT;
    }

    @Override
    protected List<Pair<Pattern, String>> getNumberReplacePatterns() {
        return NUMBER_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pattern> getNumberPatterns() {
        return NUMBER_PATTERNS;
    }

    @Override
    protected List<Pattern> getAdditionalPatterns() {
        return ADDITIONAL_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getStreetNameReplacePatterns() {
        return STREET_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getCompleteStreetNameReplacePatterns() {
        return COMPLETE_STREET_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pair<Pattern, String>> getCityReplacePatterns() {
        return CITY_REPLACE_PATTERNS;
    }

    @Override
    protected List<Pattern> getCityAdditionalPatterns() {
        return CITY_ADDITIONAL_PATTERNS;
    }

    @Override
    protected Pattern getNotAllowedStreetChars() {
        return UNALLOWED_CHARACTERS_PATTERN;
    }

}
