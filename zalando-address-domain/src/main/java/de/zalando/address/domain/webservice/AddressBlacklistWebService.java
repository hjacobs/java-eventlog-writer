package de.zalando.address.domain.webservice;

import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

import de.zalando.address.domain.blacklist.BlacklistAddressListRequest;
import de.zalando.address.domain.blacklist.BlacklistAddressSearchRequest;
import de.zalando.address.domain.blacklist.BlacklistCSVImportRequest;
import de.zalando.address.domain.blacklist.BlacklistCSVImportResponse;
import de.zalando.address.domain.blacklist.BlacklistQueryResult;
import de.zalando.address.domain.blacklist.BlacklistSaveResult;
import de.zalando.address.domain.blacklist.BlacklistUpdateListRequest;

import de.zalando.domain.exception.ServiceException;

@WebService(targetNamespace = "http://www.zalando.de/ns")
public interface AddressBlacklistWebService {

    /**
     * @return  total size of the blacklist table
     */
    int getAddressBlacklistSize() throws ServiceException;

    /**
     * @param   addressPrefix  prefix of addresses, which should be counted
     *
     * @return  count of blacklisted addresses with the given prefix
     */
    int getAddressBlacklistSizeWithPrefix(@WebParam(name = "prefix") String addressPrefix) throws ServiceException;

    /**
     * @param   request  addresses to insert
     *
     * @return  object with return enums for each given address
     */
    BlacklistSaveResult saveAddresses(@WebParam(name = "request") BlacklistAddressListRequest request)
        throws ServiceException;

    /**
     * @param   request  addresses to insert
     *
     * @return  object with return enums for each given address
     */
    BlacklistSaveResult updateAddresses(@WebParam(name = "request") BlacklistUpdateListRequest request)
        throws ServiceException;

    /**
     * @param  ids  List of the addresses to remove
     */
    void deleteAddresses(@WebParam(name = "ids") List<Integer> ids) throws ServiceException;

    /**
     * @return  all blacklisted addresses
     *
     * @throws  ServiceException
     */
    BlacklistQueryResult getAllAddresses() throws ServiceException;

    /**
     * @param   size    size of the address list
     * @param   offset  how many addresses should be skipped - to allow pagination
     *
     * @return
     *
     * @throws  ServiceException
     */
    BlacklistQueryResult getAddresses(@WebParam(name = "size") int size,
            @WebParam(name = "offset") int offset) throws ServiceException;

    /**
     * @param   request  - {@link BlacklistAddressSearchRequest} contains parameters street, zip, size and offset
     *
     * @return  {@link BlacklistQueryResult}
     *
     * @throws  ServiceException
     */
    BlacklistQueryResult searchAddresses(@WebParam(name = "searchRequest") BlacklistAddressSearchRequest request)
        throws ServiceException;

    /**
     * Imports addresses from a csv file.
     *
     * @param   request
     *
     * @return
     *
     * @throws  ServiceException
     */
    BlacklistCSVImportResponse importBlacklistAddressesFromCSV(
            @WebParam(name = "request") BlacklistCSVImportRequest request) throws ServiceException;

}
