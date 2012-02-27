package de.zalando.zomcat.jobs.batch;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class JobResponse<Item> {

    private final Item item;

    private final List<String> errorMessages;

    public JobResponse(final Item item) {
        this.item = item;
        errorMessages = Lists.newArrayList();
    }

    /**
     * Returns true if there are no error messages for this item, otherwise false.
     *
     * @return
     */
    public boolean isSuccess() {
        return errorMessages.isEmpty();
    }

    public void addErrorMessage(final String errorMessage) {
        if (!Strings.isNullOrEmpty(errorMessage)) {
            errorMessages.add(errorMessage);
        }
    }

    public void addErrorMessages(final Collection<String> errorMessage) {
        if (errorMessage != null) {
            errorMessages.addAll(errorMessage);
        }
    }

    public ImmutableList<String> getErrorMessages() {
        return ImmutableList.copyOf(errorMessages);
    }

    public Item getItem() {
        return item;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JobResponse [jobItem=");
        builder.append(item);
        builder.append(", errorMessages=");
        builder.append(errorMessages);
        builder.append(']');
        return builder.toString();
    }

}
