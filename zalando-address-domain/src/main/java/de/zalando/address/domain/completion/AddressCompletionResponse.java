package de.zalando.address.domain.completion;

import java.util.List;

import com.google.common.collect.Lists;

import com.typemapper.annotations.DatabaseField;

public class AddressCompletionResponse {

    @DatabaseField(name = "address_completion")
    private List<AddressCompletion> completions = Lists.newArrayList();

    @DatabaseField
    private AddressCompletionStatus status;

    public List<AddressCompletion> getCompletions() {
        return completions;
    }

    public void setCompletions(final List<AddressCompletion> completions) {
        this.completions = completions;
    }

    public AddressCompletionStatus getStatus() {
        return status;
    }

    public void setStatus(final AddressCompletionStatus status) {
        this.status = status;
    }
}
