package de.zalando.payment.domain.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.cxf.annotations.WSDLDocumentation;

import de.zalando.payment.domain.settings.PaymentSettings;
import de.zalando.payment.domain.settings.PaymentWebServiceException;

@WebService(targetNamespace = "http://payment.zalando.de/settings")
@WSDLDocumentation("Payment settings")
public interface PaymentSettingsWebService {

    PaymentSettings getPaymentSettings(@WebParam(name = "appDomainId") int appDomainId)
        throws PaymentWebServiceException;

}
