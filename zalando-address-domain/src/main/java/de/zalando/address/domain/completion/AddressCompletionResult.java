package de.zalando.address.domain.completion;

import java.util.ArrayList;
import java.util.List;

public class AddressCompletionResult {
    private static final long serialVersionUID = -9184818928058674247L;

    private final List<AddressCompletion> completions = new ArrayList<AddressCompletion>();

    public List<AddressCompletion> getCompletions() {

        return completions;
    }
}
