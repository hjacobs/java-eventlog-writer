package de.zalando.address.domain.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;

import de.zalando.address.domain.CustomerAddress;
import de.zalando.address.domain.checker.AddressCheckResult;
import de.zalando.address.domain.checker.AddressRoutingCodeResult;
import de.zalando.address.domain.checker.CheckerIds;
import de.zalando.address.domain.checker.ValidConfigs;
import de.zalando.address.domain.suggestion.AddressSuggestionResult;

import de.zalando.domain.shipment.ShipmentProvider;

@WebService(targetNamespace = "http://www.zalando.de/ns")
public interface AddressCheckWebService {

    AddressCheckResult checkAddressWithRefIdAndConfig(@WebParam(name = "refId") String refId,
            @WebParam(name = "address") CustomerAddress address,
            @WebParam(name = "checkerIds") final CheckerIds checkerIds);

    AddressCheckResult checkAddressWithRefId(@WebParam(name = "refId") String refId,
            @WebParam(name = "address") CustomerAddress address);

    AddressCheckResult checkAddress(@WebParam(name = "address") CustomerAddress address);

    AddressSuggestionResult suggestAddressForZip(@WebParam(name = "countryCode") String contryCode,
            @WebParam(name = "zip") String zipCode,
            @WebParam(name = "houseNumber") String houseNumber);

    ValidConfigs getValidConfigs();

    /**
     * Returns the Routing Code for an address (Leitcode) in the format
     * "[PLZ].[stra_db_str_code].[HAUSSNR].[Produktcode] [Validation digit].".
     *
     * @param   address  Given address is assumed to be already normalized and is not normalized again (fuzzy matching
     *                   is not applied).
     *
     * @return
     */
    AddressRoutingCodeResult getRouterCode(@WebParam(name = "shipmentProvider") ShipmentProvider shipmentProvider,
            @WebParam(name = "address") CustomerAddress address);

}
