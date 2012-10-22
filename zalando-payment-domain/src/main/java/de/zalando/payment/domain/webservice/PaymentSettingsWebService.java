package de.zalando.payment.domain.webservice;

import javax.jws.WebParam;
import javax.jws.WebService;

import org.apache.cxf.annotations.WSDLDocumentation;

import de.zalando.payment.domain.exception.PaymentWebServiceException;
import de.zalando.payment.domain.settings.PaymentSettings;

@WebService // (targetNamespace = PaymentServiceConstants.PAYMENT_SERVICE_NAMESPACE)
@WSDLDocumentation("Payment settings")
public interface PaymentSettingsWebService {

    PaymentSettings getPaymentSettings(@WebParam(name = "appDomainId") int appDomainId)
        throws PaymentWebServiceException;

}
