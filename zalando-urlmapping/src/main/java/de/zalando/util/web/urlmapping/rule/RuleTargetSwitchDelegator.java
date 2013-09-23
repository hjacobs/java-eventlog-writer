package de.zalando.util.web.urlmapping.rule;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;

import de.zalando.util.web.urlmapping.MappingContext;

/**
 * This delegator interface allows the user for url-mapping library to select the mapping target of a forwarding-rule.
 * Use the Builder to create a map of rule-ids and their "RuleMappingTarget.TargetType".
 */
public interface RuleTargetSwitchDelegator {

    String apply(final MappingContext context, final ForwardMappingRule ruleMappingTargets);

    String ANY_RULE = "*";

    RuleTargetSwitchDelegator DEFAULT = new Builder().add(ANY_RULE, ForwardMappingRule.TargetType.STRIPES).build();

    /**
     * RuleTargetSwitcherDelegator.Builder can be used to build a delegator that switches to different targets for.
     */
    public static class Builder {

        private ImmutableMap.Builder<String, ForwardMappingRule.TargetType> ruleTargetSettingsBuilder = ImmutableMap
                .builder();

        public Builder add(final String ruleId, final ForwardMappingRule.TargetType targetType) {
            ruleTargetSettingsBuilder.put(ruleId, targetType);
            return this;
        }

        public RuleTargetSwitchDelegator build() {

            final ImmutableMap<String, ForwardMappingRule.TargetType> ruleTargetSettings =
                ruleTargetSettingsBuilder.build();

            checkArgument(ruleTargetSettings.keySet().contains(ANY_RULE),
                "Missing default target setting. A mapping with '*' (ANY_RULE) is required.");

            return new RuleTargetSwitchDelegator() {
                @Override
                public String apply(final MappingContext context, final ForwardMappingRule mappingRule) {

                    // try to find the first rule target matching to request method:
                    final List<RuleMappingTarget> targets = mappingRule.getRuleMappingTargets();

                    final ForwardMappingRule.TargetType targetType = getPreferedRuleTarget(mappingRule);

                    if (targets.size() == 1) {
                        return targets.get(0).getTargetUrl();
                    }

                    //J-
                    Optional<RuleMappingTarget> target = Iterables.tryFind(targets,
                          // try to find by target-type:
                          new Predicate<RuleMappingTarget>() {
                              @Override
                              public boolean apply(
                                      @Nullable final RuleMappingTarget input) {

                                  return Objects.equals(targetType, input.getTargetType());
                              }
                          });
                    //J+

                    // if not found, take the first target
                    return target.isPresent() ? target.get().getTargetUrl() : targets.get(0).getTargetUrl();
                }

                private ForwardMappingRule.TargetType getPreferedRuleTarget(final ForwardMappingRule mappingRule) {
                    ForwardMappingRule.TargetType targetType = ruleTargetSettings.get(mappingRule.getId());
                    if (targetType == null) {
                        targetType = ruleTargetSettings.get(ANY_RULE);
                    }

                    return targetType;
                }
            };
        }

    }
}
