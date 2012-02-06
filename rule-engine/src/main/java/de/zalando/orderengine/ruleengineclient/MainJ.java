package de.zalando.orderengine.ruleengineclient;

import de.zalando.orderengine.ruleengine.DynamicRules;
import de.zalando.orderengine.ruleengine.ExcelBackedRulesetHolder;
import de.zalando.orderengine.ruleengine.RuleEngine;
import de.zalando.orderengine.ruleengine.Sample;

public class MainJ {

    public static void main(final String[] args) {

        RuleEngine engine = null;

        String itemId = "item1";
        int itemCount = 12;

        try {

            engine = new RuleEngine(new DynamicRules(new ExcelBackedRulesetHolder("disposition-ruleset.xls", 0)));

            Sample sample = new Sample();

            sample.put("itemID", itemId);
            sample.put("itemCount", itemCount);

            Object destination = engine.findLowestCostCandidate(sample);

            if (null == destination) {
                System.out.println("No suitable location found for item '" + itemId + "'.");
            } else {
                System.out.println("Best destination for item '" + itemId + "' is stock '" + destination + "'.");
            }

        } catch (Exception e) {
            if (engine == null) {
                System.out.println("Creating RuleEngine failed: " + e.getMessage());
            } else {
                System.out.println(engine.wrapError(e));
            }
        }
    }
}
