package de.zalando.payment.domain.settings;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import de.zalando.domain.order.PaymentMethodType;

@XmlSeeAlso({ IDealPaymentMethodSettings.class })
@XmlRootElement
public class PaymentMethodSettings {

    private PaymentMethodType paymentMethodType;

    public PaymentMethodSettings() { }

    @XmlAttribute(name = "id")
    public int getId() {
        return paymentMethodType.getId();
    }

    public void setId(final int id) {
        // nothing
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return paymentMethodType.name();
    }

    public void setName(final String name) {
        // nothing
    }

    public void setPaymentMethodType(final PaymentMethodType paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

}
