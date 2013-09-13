package de.zalando.util.web.urlmapping.rule;

import static com.google.common.base.Preconditions.checkArgument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

/**
 * Created with IntelliJ IDEA. User: abaresel Date: 9/10/13 Time: 8:53 AM
 */
public interface RuleActivationPredicate extends Predicate<MappingRule> {

    /**
     * Default Rule that matches any MappingRule.
     */
    RuleActivationPredicate ALL_ACTIVE = new RuleActivationPredicate() {
        @Override
        public boolean apply(@Nullable final MappingRule input) {
            return true;
        }
    };

    public static class Builder {

        public static RuleActivationPredicate deactivateById(final String mappingRuleId) {
            return new RuleActivationPredicate() {
                @Override
                public boolean apply(@Nullable final MappingRule input) {
                    return !input.getId().equals(mappingRuleId);
                }
            };
        }

        public static RuleActivationPredicate deactivatedByIds(@Nonnull final Iterable<String> deactivatedRules) {
            checkArgument(deactivatedRules != null, "Argument deactivatedRules must not be null.");

            final ImmutableSet<String> deactivatedRuleSet = ImmutableSet.copyOf(deactivatedRules);

            return new RuleActivationPredicate() {
                @Override
                public boolean apply(@Nullable final MappingRule input) {
                    return !deactivatedRuleSet.contains(input.getId());
                }
            };
        }
    }

}
