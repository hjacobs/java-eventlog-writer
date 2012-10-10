package de.zalando.address.domain.suggestion;

import java.util.ArrayList;
import java.util.List;

import com.typemapper.annotations.DatabaseField;

public class AddressSuggestionResult {

    @DatabaseField(name = "suggestions")
    private List<AddressSuggestion> suggestions = new ArrayList<AddressSuggestion>();

    @DatabaseField(name = "result_status_msg")
    private AddressSuggestionStatus status;

    @DatabaseField(name = "total_results")
    private int totalResults;

    public List<AddressSuggestion> getSuggestions() {
        return suggestions;
    }

    public AddressSuggestionStatus getStatus() {
        return status;
    }

    public void setStatus(final AddressSuggestionStatus status) {
        this.status = status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(final int totalResults) {
        this.totalResults = totalResults;
    }

}
