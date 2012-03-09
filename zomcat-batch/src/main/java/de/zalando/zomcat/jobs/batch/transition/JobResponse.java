package de.zalando.zomcat.jobs.batch.transition;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class JobResponse<Item> {

    public static final String NO_RESPONSE_AFTER_PROCESSING =
        "No response given after processing of item (null). Marking as failed.";

    private final Item jobItem;

    private final List<String> errorMessages;

    public JobResponse(final Item item) {
        jobItem = item;
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

    public Item getJobItem() {
        return jobItem;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("JobResponse [jobItem=");
        builder.append(jobItem);
        builder.append(", errorMessages=");
        builder.append(errorMessages);
        builder.append(']');
        return builder.toString();
    }

}
