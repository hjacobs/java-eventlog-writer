package de.zalando.payment.domain.settings;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement
@XmlType(propOrder = {"enabledBankGroupTypes", "disabledBankGroupTypes"})
public class IDealPaymentMethodSettings extends PaymentMethodSettings {

    private List<BankGroupType> enabledBankGroupTypes = new ArrayList<BankGroupType>();
    private List<BankGroupType> disabledBankGroupTypes = new ArrayList<BankGroupType>();

    public IDealPaymentMethodSettings() { }

    public IDealPaymentMethodSettings(final List<BankGroupType> enabledBankGroupTypes,
            final List<BankGroupType> disabledBankGroupTypes) {
        this.enabledBankGroupTypes = enabledBankGroupTypes;
        this.disabledBankGroupTypes = disabledBankGroupTypes;
    }

    @XmlElementWrapper(name = "enabledBankGroupTypes", nillable = false, required = true)
    @XmlElement(name = "bankGroupType", nillable = false, required = true)
    public List<BankGroupType> getEnabledBankGroupTypes() {
        return enabledBankGroupTypes;
    }

    public void setEnabledBankGroupTypes(final List<BankGroupType> enabledBankGroupTypes) {
        this.enabledBankGroupTypes = enabledBankGroupTypes;
    }

    @XmlElementWrapper(name = "disabledBankGroupTypes", nillable = false, required = true)
    @XmlElement(name = "bankGroupType", nillable = false, required = true)
    public List<BankGroupType> getDisabledBankGroupTypes() {
        return disabledBankGroupTypes;
    }

    public void setDisabledBankGroupTypes(final List<BankGroupType> disabledBankGroupTypes) {
        this.disabledBankGroupTypes = disabledBankGroupTypes;
    }

}
