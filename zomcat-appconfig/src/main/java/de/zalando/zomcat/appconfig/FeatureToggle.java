package de.zalando.zomcat.appconfig;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

public abstract class FeatureToggle {

    private static final String FEATURE = "feature.";

    private final String toggleName;

    private final String appConfigName;

    private final String description;

    public FeatureToggle(final String toggleName, final String description) {
        checkArgument(!isNullOrEmpty(toggleName), "toggle name must not be empty or null");
        checkArgument(!toggleName.startsWith(FEATURE), "toggle name must not start with 'feature.'");
        checkArgument(!isNullOrEmpty(description), "description must not be empty or null");

        this.toggleName = toggleName;
        this.appConfigName = FEATURE + toggleName;
        this.description = description;
    }

    public final String getToggleName() {
        return toggleName;
    }

    public String getAppConfigName() {
        return appConfigName;
    }

    public final String getDescription() {
        return description;
    }
}
