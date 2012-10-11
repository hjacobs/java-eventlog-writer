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
public class BritishAddressProcessorGuessTest {

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
            {"Edinburgh ", "B45 8EH", "120 Leach Green Lane", "Edinburgh", "B45 8EH", "120 Leach Green Lane",  null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield Road West", "Edinburgh", "EH15 1RH", "13 Southfield Road West",null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield Road West", "Edinburgh", "EH15 1RH", "13 Southfield Road West", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield Road East", "Edinburgh", "EH15 1RH", "13 Southfield Road East", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield Road North", "Edinburgh", "EH15 1RH", "13 Southfield Road North", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield Road South", "Edinburgh", "EH15 1RH", "13 Southfield Road South", null, null},

            {"Edinburgh ", "EH15 1RH", "13 Southfield West Road", "Edinburgh", "EH15 1RH", "13 Southfield West Road", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield East Road", "Edinburgh", "EH15 1RH", "13 Southfield East Road", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield North Road", "Edinburgh", "EH15 1RH", "13 Southfield North Road", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield South Road", "Edinburgh", "EH15 1RH", "13 Southfield South Road", null, null},

            {"Edinburgh ", "EH15 1RH", "13 Southfield Road West East North South", "Edinburgh", "EH15 1RH","13 Southfield Road West East North South", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield West East North South Road", "Edinburgh", "EH15 1RH","13 Southfield West East North South Road", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield North Road", "Edinburgh", "EH15 1RH", "13 Southfield North Road", null, null},
            {"Edinburgh ", "EH15 1RH", "13 Southfield South Road", "Edinburgh", "EH15 1RH", "13 Southfield South Road", null, null},

            {"London", "EC1A 4ER", "Castle Exchange 41 Broad Street", "London", "EC1A 4ER", "Castle Exchange 41 Broad Street", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Road 23 Hemlingford Road", "London", "EC1A 4ER","22 Hemlingford Road 23 Hemlingford Road", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford rd mt.", "London", "EC1A 4ER", "22 Hemlingford Road Mount", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Road", "London", "EC1A 4ER", "22 Hemlingford Road", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford RD", "London", "EC1A 4ER", "22 Hemlingford Road", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford rD", "London", "EC1A 4ER", "22 Hemlingford Road", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Rd", "London", "EC1A 4ER", "22 Hemlingford Road", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford rd", "London", "EC1A 4ER", "22 Hemlingford Road", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford rd.", "London", "EC1A 4ER", "22 Hemlingford Road", null, null},
            {"London", "EC1A 4ER", "22 rd Hemlingford", "London", "EC1A 4ER", "22 Road Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Avenue ", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford AV", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Av", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford aV", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford av", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford av.", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 av Hemlingford", "London", "EC1A 4ER", "22 Avenue Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Avenue ", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford AVE", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Ave", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford aVE", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford ave", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford ave.", "London", "EC1A 4ER", "22 Hemlingford Avenue", null, null},
            {"London", "EC1A 4ER", "22 ave Hemlingford", "London", "EC1A 4ER", "22 Avenue Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Close", "London", "EC1A 4ER", "22 Hemlingford Close", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford CL", "London", "EC1A 4ER", "22 Hemlingford Close", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cl", "London", "EC1A 4ER", "22 Hemlingford Close", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Cl", "London", "EC1A 4ER", "22 Hemlingford Close", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cl", "London", "EC1A 4ER", "22 Hemlingford Close", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cl.", "London", "EC1A 4ER", "22 Hemlingford Close", null, null},
            {"London", "EC1A 4ER", "22 cl Hemlingford", "London", "EC1A 4ER", "22 Close Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Cottages", "London", "EC1A 4ER", "22 Hemlingford Cottages", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford COTTS", "London", "EC1A 4ER", "22 Hemlingford Cottages", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cotTs", "London", "EC1A 4ER", "22 Hemlingford Cottages", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford CotTs", "London", "EC1A 4ER", "22 Hemlingford Cottages", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cotts", "London", "EC1A 4ER", "22 Hemlingford Cottages", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cotts.", "London", "EC1A 4ER", "22 Hemlingford Cottages", null, null},
            {"London", "EC1A 4ER", "22 cotts Hemlingford", "London", "EC1A 4ER", "22 Cottages Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Court", "London", "EC1A 4ER", "22 Hemlingford Court", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford CT", "London", "EC1A 4ER", "22 Hemlingford Court", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cT", "London", "EC1A 4ER", "22 Hemlingford Court", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Ct", "London", "EC1A 4ER", "22 Hemlingford Court", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford ct", "London", "EC1A 4ER", "22 Hemlingford Court", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford ct.", "London", "EC1A 4ER", "22 Hemlingford Court", null, null},
            {"London", "EC1A 4ER", "22 ct Hemlingford", "London", "EC1A 4ER", "22 Court Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Crescent", "London", "EC1A 4ER", "22 Hemlingford Crescent", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford CRES", "London", "EC1A 4ER", "22 Hemlingford Crescent", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford CRes", "London", "EC1A 4ER", "22 Hemlingford Crescent", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford crES", "London", "EC1A 4ER", "22 Hemlingford Crescent", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cres", "London", "EC1A 4ER", "22 Hemlingford Crescent", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford cres.", "London", "EC1A 4ER", "22 Hemlingford Crescent", null, null},
            {"London", "EC1A 4ER", "22 cres Hemlingford", "London", "EC1A 4ER", "22 Crescent Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Drive", "London", "EC1A 4ER", "22 Hemlingford Drive", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford DR", "London", "EC1A 4ER", "22 Hemlingford Drive", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Dr", "London", "EC1A 4ER", "22 Hemlingford Drive", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford dR", "London", "EC1A 4ER", "22 Hemlingford Drive", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford dr", "London", "EC1A 4ER", "22 Hemlingford Drive", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford dr.", "London", "EC1A 4ER", "22 Hemlingford Drive", null, null},
            {"London", "EC1A 4ER", "22 dr Hemlingford", "London", "EC1A 4ER", "22 Drive Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Estate", "London", "EC1A 4ER", "22 Hemlingford Estate", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford EST", "London", "EC1A 4ER", "22 Hemlingford Estate", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford ESt", "London", "EC1A 4ER", "22 Hemlingford Estate", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford esT", "London", "EC1A 4ER", "22 Hemlingford Estate", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford est", "London", "EC1A 4ER", "22 Hemlingford Estate", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford est.", "London", "EC1A 4ER", "22 Hemlingford Estate", null, null},
            {"London", "EC1A 4ER", "22 est Hemlingford", "London", "EC1A 4ER", "22 Estate Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Field", "London", "EC1A 4ER", "22 Hemlingford Field", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford FLD", "London", "EC1A 4ER", "22 Hemlingford Field", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Fld", "London", "EC1A 4ER", "22 Hemlingford Field", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford fLD", "London", "EC1A 4ER", "22 Hemlingford Field", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford fld", "London", "EC1A 4ER", "22 Hemlingford Field", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford fld.", "London", "EC1A 4ER", "22 Hemlingford Field", null, null},
            {"London", "EC1A 4ER", "22 fld Hemlingford", "London", "EC1A 4ER", "22 Field Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Fields", "London", "EC1A 4ER", "22 Hemlingford Fields", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford FLDS", "London", "EC1A 4ER", "22 Hemlingford Fields", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Flds", "London", "EC1A 4ER", "22 Hemlingford Fields", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford fLDS", "London", "EC1A 4ER", "22 Hemlingford Fields", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford flds", "London", "EC1A 4ER", "22 Hemlingford Fields", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford flds.", "London", "EC1A 4ER", "22 Hemlingford Fields", null, null},
            {"London", "EC1A 4ER", "22 flds Hemlingford", "London", "EC1A 4ER", "22 Fields Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Gardens", "London", "EC1A 4ER", "22 Hemlingford Gardens", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford GDNS", "London", "EC1A 4ER", "22 Hemlingford Gardens", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford GDns", "London", "EC1A 4ER", "22 Hemlingford Gardens", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford gdNS", "London", "EC1A 4ER", "22 Hemlingford Gardens", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford gdns", "London", "EC1A 4ER", "22 Hemlingford Gardens", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford gdns.", "London", "EC1A 4ER", "22 Hemlingford Gardens", null, null},
            {"London", "EC1A 4ER", "22 gdns Hemlingford", "London", "EC1A 4ER", "22 Gardens Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Green", "London", "EC1A 4ER", "22 Hemlingford Green", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford GRN", "London", "EC1A 4ER", "22 Hemlingford Green", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford GRn", "London", "EC1A 4ER", "22 Hemlingford Green", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford gRN", "London", "EC1A 4ER", "22 Hemlingford Green", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford grn", "London", "EC1A 4ER", "22 Hemlingford Green", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford grn.", "London", "EC1A 4ER", "22 Hemlingford Green", null, null},
            {"London", "EC1A 4ER", "22 grn Hemlingford", "London", "EC1A 4ER", "22 Green Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Grove", "London", "EC1A 4ER", "22 Hemlingford Grove", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford GR", "London", "EC1A 4ER", "22 Hemlingford Grove", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Gr", "London", "EC1A 4ER", "22 Hemlingford Grove", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford gR", "London", "EC1A 4ER", "22 Hemlingford Grove", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford gr", "London", "EC1A 4ER", "22 Hemlingford Grove", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford gr.", "London", "EC1A 4ER", "22 Hemlingford Grove", null, null},
            {"London", "EC1A 4ER", "22 gr Hemlingford", "London", "EC1A 4ER", "22 Grove Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Lane", "London", "EC1A 4ER", "22 Hemlingford Lane", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford LA", "London", "EC1A 4ER", "22 Hemlingford Lane", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford La", "London", "EC1A 4ER", "22 Hemlingford Lane", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford lA", "London", "EC1A 4ER", "22 Hemlingford Lane", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford la", "London", "EC1A 4ER", "22 Hemlingford Lane", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford la.", "London", "EC1A 4ER", "22 Hemlingford Lane", null, null},
            {"London", "EC1A 4ER", "22 la Hemlingford", "London", "EC1A 4ER", "22 Lane Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Mount", "London", "EC1A 4ER", "22 Hemlingford Mount", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford MT", "London", "EC1A 4ER", "22 Hemlingford Mount", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Mt", "London", "EC1A 4ER", "22 Hemlingford Mount", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford mT", "London", "EC1A 4ER", "22 Hemlingford Mount", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford mt", "London", "EC1A 4ER", "22 Hemlingford Mount", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford mt.", "London", "EC1A 4ER", "22 Hemlingford Mount", null, null},
            {"London", "EC1A 4ER", "22 mt Hemlingford", "London", "EC1A 4ER", "22 Mount Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Parade", "London", "EC1A 4ER", "22 Hemlingford Parade", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford PDE", "London", "EC1A 4ER", "22 Hemlingford Parade", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford PDe", "London", "EC1A 4ER", "22 Hemlingford Parade", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pdE", "London", "EC1A 4ER", "22 Hemlingford Parade", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pde", "London", "EC1A 4ER", "22 Hemlingford Parade", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pde.", "London", "EC1A 4ER", "22 Hemlingford Parade", null, null},
            {"London", "EC1A 4ER", "22 pde Hemlingford", "London", "EC1A 4ER", "22 Parade Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Park", "London", "EC1A 4ER", "22 Hemlingford Park", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford PK", "London", "EC1A 4ER", "22 Hemlingford Park", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Pk", "London", "EC1A 4ER", "22 Hemlingford Park", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pK", "London", "EC1A 4ER", "22 Hemlingford Park", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pk", "London", "EC1A 4ER", "22 Hemlingford Park", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pk.", "London", "EC1A 4ER", "22 Hemlingford Park", null, null},
            {"London", "EC1A 4ER", "22 pk Hemlingford", "London", "EC1A 4ER", "22 Park Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Place", "London", "EC1A 4ER", "22 Hemlingford Place", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford PL", "London", "EC1A 4ER", "22 Hemlingford Place", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Pl", "London", "EC1A 4ER", "22 Hemlingford Place", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pL", "London", "EC1A 4ER", "22 Hemlingford Place", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pl", "London", "EC1A 4ER", "22 Hemlingford Place", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford pl.", "London", "EC1A 4ER", "22 Hemlingford Place", null, null},
            {"London", "EC1A 4ER", "22 pl Hemlingford", "London", "EC1A 4ER", "22 Place Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Square", "London", "EC1A 4ER", "22 Hemlingford Square", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford SQ", "London", "EC1A 4ER", "22 Hemlingford Square", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Sq", "London", "EC1A 4ER", "22 Hemlingford Square", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford sQ", "London", "EC1A 4ER", "22 Hemlingford Square", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford sq", "London", "EC1A 4ER", "22 Hemlingford Square", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford sq.", "London", "EC1A 4ER", "22 Hemlingford Square", null, null},
            {"London", "EC1A 4ER", "22 sq Hemlingford", "London", "EC1A 4ER", "22 Square Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Terrace", "London", "EC1A 4ER", "22 Hemlingford Terrace", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford TER", "London", "EC1A 4ER", "22 Hemlingford Terrace", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford TEr", "London", "EC1A 4ER", "22 Hemlingford Terrace", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford tER", "London", "EC1A 4ER", "22 Hemlingford Terrace", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford ter", "London", "EC1A 4ER", "22 Hemlingford Terrace", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford ter.", "London", "EC1A 4ER", "22 Hemlingford Terrace", null, null},
            {"London", "EC1A 4ER", "22 ter Hemlingford", "London", "EC1A 4ER", "22 Terrace Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Walk", "London", "EC1A 4ER", "22 Hemlingford Walk", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford WK", "London", "EC1A 4ER", "22 Hemlingford Walk", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Wk", "London", "EC1A 4ER", "22 Hemlingford Walk", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford wK", "London", "EC1A 4ER", "22 Hemlingford Walk", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford wk", "London", "EC1A 4ER", "22 Hemlingford Walk", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford wk.", "London", "EC1A 4ER", "22 Hemlingford Walk", null, null},
            {"London", "EC1A 4ER", "22 wk Hemlingford", "London", "EC1A 4ER", "22 Walk Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford Way", "London", "EC1A 4ER", "22 Hemlingford Way", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford WY", "London", "EC1A 4ER", "22 Hemlingford Way", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford Wy", "London", "EC1A 4ER", "22 Hemlingford Way", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford wY", "London", "EC1A 4ER", "22 Hemlingford Way", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford wy", "London", "EC1A 4ER", "22 Hemlingford Way", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford wy.", "London", "EC1A 4ER", "22 Hemlingford Way", null, null},
            {"London", "EC1A 4ER", "22 wy Hemlingford", "London", "EC1A 4ER", "22 Way Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford West", "London", "EC1A 4ER", "22 Hemlingford West", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford W", "London", "EC1A 4ER", "22 Hemlingford West", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford w", "London", "EC1A 4ER", "22 Hemlingford West", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford w.", "London", "EC1A 4ER", "22 Hemlingford West", null, null},
            {"London", "EC1A 4ER", "22 w Hemlingford", "London", "EC1A 4ER", "22 West Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford East", "London", "EC1A 4ER", "22 Hemlingford East", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford E", "London", "EC1A 4ER", "22 Hemlingford East", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford e", "London", "EC1A 4ER", "22 Hemlingford East", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford e.", "London", "EC1A 4ER", "22 Hemlingford East", null, null},
            {"London", "EC1A 4ER", "22 e Hemlingford", "London", "EC1A 4ER", "22 East Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford North", "London", "EC1A 4ER", "22 Hemlingford North", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford N", "London", "EC1A 4ER", "22 Hemlingford North", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford n", "London", "EC1A 4ER", "22 Hemlingford North", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford n.", "London", "EC1A 4ER", "22 Hemlingford North", null, null},
            {"London", "EC1A 4ER", "22 n Hemlingford", "London", "EC1A 4ER", "22 North Hemlingford", null, null},

            {"London", "EC1A 4ER", "22 Hemlingford South", "London", "EC1A 4ER", "22 Hemlingford South", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford South back door", "London", "EC1A 4ER", "22 Hemlingford South Back Door", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford S", "London", "EC1A 4ER", "22 Hemlingford South", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford s", "London", "EC1A 4ER", "22 Hemlingford South", null, null},
            {"London", "EC1A 4ER", "22 Hemlingford s.", "London", "EC1A 4ER", "22 Hemlingford South", null, null},
            {"London", "EC1A 4ER", "22 s Hemlingford", "London", "EC1A 4ER", "22 South Hemlingford", null, null},
            {"London", "EC1A 4ER", "s Hemlingford", "London", "EC1A 4ER", "South Hemlingford", null, null},

            //{"London", "EC1A 4ER", "22 n Hemlingford Lane South back door", "London", "EC1A 4ER", "North Hemlingford Lane South", null, "back door"},

            {"London", "EC1A 4ER", "61 St Martin's Le Grand", "London", "EC1A 4ER", "61 St Martin's Le Grand", null, null},
            {"London", "SW1Y 4PD", "1 St James's Square", "London", "SW1Y 4PD", "1 St James's Square", null, null},
            {"Liverpool", "L3 9SJ", "St Paul`s Square", "Liverpool", "L3 9SJ", "St Paul`s Square", null, null},
            {"London", "EC3V 9", "12 Saint Michael's Alley", "London", "EC 3V9", "12 Saint Michael's Alley", null, null},
            {"Nantwich", "CW55UH", "22 Lambert Crescent", "Nantwich", "CW5 5UH", "22 Lambert Crescent", null, null},
            {"London", "SE288AH", "39 Malthus Path", "London", "SE28 8AH", "39 Malthus Path", null, null},
            {"Bargeddie,baillieston,glasgow", "G697RS", "217 Langmuir Road", "Bargeddie, Baillieston, Glasgow", "G69 7RS", "217 Langmuir Road", null, null},
            {"Glasgow", "G320RL", "34 Carrick Drive", "Glasgow", "G32 0RL", "34 Carrick Drive", null, null},
            {"Godstone", "RH98AB", "34 Salisbury Road", "Godstone", "RH9 8AB", "34 Salisbury Road", null, null},
            {"London", "E113JW", "17 Ranelagh Road", "London", "E11 3JW", "17 Ranelagh Road", null, null},
            {"London", " E 62 P S", "36 Sussex Road eastham", "London", "E6 2PS", "36 Sussex Road Eastham", null, null},
            {"London", "N1 70TZ", "65 Blaydon Close", "London", "N17 0TZ", "65 Blaydon Close", null, null},
            {"Bath", "bA13e J", "123 Locksbrook Road", "Bath", "BA1 3EJ", "123 Locksbrook Road", null, null},
            {"Barking", "IG  \t             117QE", "131 St. Awdrys Road Barking", "Barking", "IG11 7QE","131 St. Awdrys Road Barking", null, null},
            {"Aberdeen", "AB166RP", "53 Burnbrae Crescent mastrick", "Aberdeen", "AB16 6RP", "53 Burnbrae Crescent Mastrick", null, null},
            {"Heage,belper", "DE562AB", "148 Park Road Heage. Belper", "Heage, Belper", "DE56 2AB", "148 Park Road Heage. Belper", null, null},
            {"London", "E126EN", "115 Second Avenue manor park", "London", "E12 6EN", "115 Second Avenue Manor Park", null, null},
            {"Chippenham", "SN146XX", "1 Woodpecker Mews Chippenham", "Chippenham", "SN14 6XX", "1 Woodpecker Mews Chippenham", null, null},
            {"Salford", "M71ZN", "252 camp Street", "Salford", "M7 1ZN", "252 Camp Street", null, null},
            {"Wirral", "CH623NS", "43 Terminus Road bromborough wirral", "Wirral", "CH62 3NS", "43 Terminus Road Bromborough Wirral", null, null},
            {"London", "N176AT", "67 Belmont Road", "London", "N17 6AT", "67 Belmont Road", null, null},
            {"Colchester", "CO43HX", "59b Parsons Heath", "Colchester", "CO4 3HX", "59b Parsons Heath", null, null},
            {"Leicester", "LE4 6DR", "3 Keats Walk", "Leicester", "LE4 6DR", "3 Keats Walk", null, null},
            {"Poole", "BH165SB", "90 Tree Hamlets Upton", "Poole", "BH16 5SB", "90 Tree Hamlets Upton", null, null},
            {"Ormesby,middlesbrough", "TS 79LQ", "17 Norham Walk overfields", "Ormesby, Middlesbrough", "TS7 9LQ","17 Norham Walk Overfields", null, null},
            {"Kingsbury,tamworth", "  B782NJ   ", "22 Hemlingford Road", "Kingsbury, Tamworth", "B78 2NJ","22 Hemlingford Road", null, null},
            {"London", "SE152RQ", "10 Culmore Road", "London", "SE15 2RQ", "10 Culmore Road", null, null},
            {"London", "SE135QX", "21 Cordwell Road", "London", "SE13 5QX", "21 Cordwell Road", null, null},
            {"Coventry", "CV63FB", "12 Grangemouth Road", "Coventry", "CV6 3FB", "12 Grangemouth Road", null, null},
            {"London", "E70HG", "44 Stracey Road", "London", "E7 0HG", "44 Stracey Road", null, null},
            {"Sheerness", "ME122AT", "99 Alexandra Road", "Sheerness", "ME12 2AT", "99 Alexandra Road", null, null},
            {"London", "SE228JL", "7 Plough Lane Dulwich", "London", "SE22 8JL", "7 Plough Lane Dulwich", null, null},
            {"Swindon", "SN15DT", "8 Tennyson Street", "Swindon", "SN1 5DT", "8 Tennyson Street", null, null},
            {"Bromley", "BR28QE", "37 Beverley Road", "Bromley", "BR2 8QE", "37 Beverley Road", null, null},
            {"London", "SE288LJ", "45 Blyth Road", "London", "SE28 8LJ", "45 Blyth Road", null, null},
            {"Chatham", "ME45TA", "17 Chalkpit Hill", "Chatham", "ME4 5TA", "17 Chalkpit Hill", null, null},
            {"Ashill,thetford", "IP257BH", "4 Lewis Close ashill", "Ashill, Thetford", "IP25 7BH", "4 Lewis Close Ashill", null, null},
            {"London", "EC2M4PL", "8 Devonshire Square 8 Devonshire Square", "London", "EC2M 4PL","8 Devonshire Square 8 Devonshire Square", null, null},
            {"Woking", "GU214HT", "26 Fairlawn Park", "Woking", "GU21 4HT", "26 Fairlawn Park", null, null},
            {"Liss", "GU337JX", "6, 35 Longmead", "Liss", "GU33 7JX", "6, 35 Longmead", null, null},
            {"Liss", "GU337JX", "6 35 Longmead", "Liss", "GU33 7JX", "6 35 Longmead", null, null},
            {"Aldermaston Reading Berkshire", "RG78JN,", "Unit 4 Zephyr House Calleva Park", "Aldermaston Reading","RG7 8JN", "Unit 4 Zephyr House Calleva Park", null, null},
            {"Stokenchurch, High Wycombe, Buckinghamshire", "HP143TB,", "29 Chalk Farm Rd","Stokenchurch, High Wycombe", "HP14 3TB", "29 Chalk Farm Road", null, null},
            {"London", "SW178EN,", "2/53 Foxbourne Road", "London", "SW17 8EN", "2/53 Foxbourne Road", null, null},
            {"London", "SW178EN,", "Flat 2/53 Foxbourne Road", "London", "SW17 8EN", "Flat 2/53 Foxbourne Road", null ,null},
            {"London", "SW178EN,", "Flat 2 Foxbourne Road", "London", "SW17 8EN", "Flat 2 Foxbourne Road", null, null},
            {"Cambridge", "CB28BL,", "27 The Copper Building Kingfisher Way", "Cambridge", "CB2 8BL","27 The Copper Building Kingfisher Way", null, null},
            {"Headley Down, Bordon, Hampshire", "GU358JS", "Gower Cottage, Westview Rd", "Headley Down, Bordon","GU35 8JS", "Gower Cottage, Westview Road", null, null},
            {"London", "SE11EN", "Langdale House 11 Marshalsea Rd", "London", "SE1 1EN", "Langdale House 11 Marshalsea Road", null, null},
            {"London", "E1W 1AT", "30 Ivory House", "London", "E1W 1AT", "30 Ivory House", null, null},

            // {"Dorking", "RH41AZ", "White Sons 104 High Street",
            // "Dorking", "RH41AZ", "High Street", "White Sons 104", null},
            {"Aylesbury, Buckinghamshire", "HP198HT", "Ardenham Court, Oxford Rd", "Aylesbury", "HP19 8HT","Ardenham Court, Oxford Road", null, null},
            {"Aylesbury, Buckinghamshire", "HP198HT", "Ardenham Court Oxford Road", "Aylesbury", "HP19 8HT","Ardenham Court Oxford Road", null, null},
            {"Aylesbury, Buckinghamshire", "HP198HT", "Ardenham Court Oxford Rd", "Aylesbury", "HP19 8HT","Ardenham Court Oxford Road", null, null},

            /*
             * {"Evesham", "WR112QW", "12 Old School Court", "Evesham",
             * "WR11 2QW", "Old School Court", "12", null},
             */
            {"Bradford, West Yorkshire", "BD3 9TF", "Lonsdale Works, Gibson Street", "Bradford", "BD3 9TF","Lonsdale Works, Gibson Street", null, null},
            {"Liverpool, Merseyside", "L1 6JD", "Millennium House, 60 Victoria Street", "Liverpool", "L1 6JD","Millennium House, 60 Victoria Street", null, null},
            {"William Brown Street, Liverpool", "L3 8EN", "Horseshoe Gallery, World Museum", "William Brown Street, Liverpool", "L3 8EN", "Horseshoe Gallery, World Museum", null, null},
            {"Brighton, East Sussex", "BN25NE", "18 Reading Road", "Brighton", "BN2 5NE", "18 Reading Road", null, null},
            {"Grays", "RM164LT", "13 Biggin Lane", "Grays", "RM16 4LT", "13 Biggin Lane", null, null},
            {"Grays", "RM164LT", "Tally Ho Riding School 13 Biggin Lane", "Grays", "RM16 4LT", "Tally Ho Riding School 13 Biggin Lane", null,  null},
            {"Grays", "RM164LT", "Tally Ho Riding School, 13 Biggin Lane", "Grays", "RM16 4LT", "Tally Ho Riding School, 13 Biggin Lane", null, null},
            {"Alton", "GU34 1EN", "110 High St", "Alton", "GU34 1EN", "110 High Street", null, null}
        //J+
        };
        return Arrays.asList(data);
    }

    public BritishAddressProcessorGuessTest(final String city, final String zip, final String streetName,
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
        final Address address = AddressBuilder.forCountry(ISOCountryCode.GB).city(city).zip(zip)
                                              .streetWithHouseNumber(streetName).build();
        assertThat(String.format("orig street [%s] street name", streetName), address.getStreetName(),
            is(expectedName));
        assertThat(String.format("orig street [%s] additional", streetName), address.getAdditional(),
            is(expectedAdditional));
        assertThat(String.format("orig street [%s] city", streetName), address.getCity(), is(expectedCity));
        assertThat(String.format("orig street [%s] zip", streetName), address.getZip(), is(expectedZip));
        assertThat(String.format("orig street [%s] house number", streetName), address.getHouseNumber(),
            is(expectedNr));
        // }
    }
}
