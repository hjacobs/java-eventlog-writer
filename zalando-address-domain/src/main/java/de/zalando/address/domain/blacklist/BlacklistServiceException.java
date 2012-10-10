package de.zalando.address.domain.blacklist;

import java.util.ArrayList;
import java.util.List;

import de.zalando.address.domain.CustomerAddress;

import de.zalando.domain.exception.ServiceException;

public class BlacklistServiceException extends ServiceException {

    private static final long serialVersionUID = -5306432408832551325L;

    private List<CustomerAddress> addresses = new ArrayList<CustomerAddress>();

    private List<Integer> ids = new ArrayList<Integer>();

    public BlacklistServiceException(final String message) {
        super(message);
    }

    public BlacklistServiceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public BlacklistServiceException(final Throwable cause) {
        super(cause);
    }

    public BlacklistServiceException(final String message, final List<CustomerAddress> addresses) {
        super(message);
        this.addresses = addresses;
    }

    public List<CustomerAddress> getAddresses() {
        return addresses;
    }

    public void setAddresses(final List<CustomerAddress> addresses) {
        this.addresses = addresses;
    }

    public List<Integer> getIds() {
        return ids;
    }

    public void setIds(final List<Integer> ids) {
        this.ids = ids;
    }
}
