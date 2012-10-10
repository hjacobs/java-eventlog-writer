package de.zalando.address.domain.blacklist;

import static java.util.Collections.max;

import static com.google.common.collect.Lists.newArrayList;

import javax.activation.DataHandler;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import au.com.bytecode.opencsv.CSVParser;

@XmlType(
    propOrder = {
        "normalize", "streetWithNumberIndex", "cityIndex", "zipIndex", "countryCodeIndex", "csvFile",
        "separatorCharacter", "quotingCharacter", "escapeCharacter", "encoding"
    }
)
public class BlacklistCSVImportRequest {

    private boolean normalize;

    private int streetWithNumberIndex;

    private int cityIndex;

    private int zipIndex;

    private int countryCodeIndex;

    private String separatorCharacter = String.valueOf(CSVParser.DEFAULT_SEPARATOR);

    private String quotingCharacter = String.valueOf(CSVParser.DEFAULT_QUOTE_CHARACTER);

    private String escapeCharacter = String.valueOf(CSVParser.DEFAULT_ESCAPE_CHARACTER);

    private DataHandler csvFile;

    private static final String DEFAULT_ENCODING = "UTF-8";

    private String encoding = DEFAULT_ENCODING;

    @XmlElement(name = "normalize", nillable = false, required = true)
    public boolean isNormalize() {
        return normalize;
    }

    public void setNormalize(final boolean normalize) {
        this.normalize = normalize;
    }

    @XmlElement(name = "streetWithNumberIndex", nillable = false, required = true)
    public int getStreetWithNumberIndex() {
        return streetWithNumberIndex;
    }

    public void setStreetWithNumberIndex(final int streetWithNumberIndex) {
        this.streetWithNumberIndex = streetWithNumberIndex;
    }

    @XmlElement(name = "cityIndex", nillable = false, required = true)
    public int getCityIndex() {
        return cityIndex;
    }

    public void setCityIndex(final int cityIndex) {
        this.cityIndex = cityIndex;
    }

    @XmlElement(name = "zipIndex", nillable = false, required = true)
    public int getZipIndex() {
        return zipIndex;
    }

    public void setZipIndex(final int zipIndex) {
        this.zipIndex = zipIndex;
    }

    @XmlElement(name = "countryCodeIndex", nillable = false, required = true)
    public int getCountryCodeIndex() {
        return countryCodeIndex;
    }

    public void setCountryCodeIndex(final int countryCodeIndex) {
        this.countryCodeIndex = countryCodeIndex;
    }

    @XmlElement(name = "csvFile", nillable = false, required = true)
    public DataHandler getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(final DataHandler csvFile) {
        this.csvFile = csvFile;
    }

    @XmlElement(name = "separatorCharacter", nillable = false, required = false)
    public String getSeparatorCharacter() {
        return separatorCharacter;
    }

    public void setSeparatorCharacter(final String separatorCharacter) {
        this.separatorCharacter = separatorCharacter;
    }

    @XmlElement(name = "quotingCharacter", nillable = false, required = false)
    public String getQuotingCharacter() {
        return quotingCharacter;
    }

    public void setQuotingCharacter(final String quotingCharacter) {
        this.quotingCharacter = quotingCharacter;
    }

    @XmlElement(name = "escapeCharacter", nillable = false, required = false)
    public String getEscapeCharacter() {
        return escapeCharacter;
    }

    public void setEscapeCharacter(final String escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    @XmlElement(name = "encoding", nillable = false, required = false)
    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public int getMaxIndex() {
        return max(newArrayList(streetWithNumberIndex, zipIndex, cityIndex, countryCodeIndex));
    }

}
