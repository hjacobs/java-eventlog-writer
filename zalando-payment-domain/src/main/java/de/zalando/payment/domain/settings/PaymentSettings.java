package de.zalando.payment.domain.settings;

import java.util.List;

import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PaymentSettings {

    private List<PaymentMethodSettings> paymentMethodSettings;

    public PaymentSettings() { }

    @XmlElementRef
    @XmlElementWrapper(name = "paymentMethodSettings")
    public List<PaymentMethodSettings> getPaymentMethodSettings() {
        return paymentMethodSettings;
    }

    public void setPaymentMethodSettings(final List<PaymentMethodSettings> paymentMethodSettings) {
        this.paymentMethodSettings = paymentMethodSettings;
    }
}
