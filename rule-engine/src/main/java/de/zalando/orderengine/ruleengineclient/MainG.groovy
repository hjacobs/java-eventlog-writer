package de.zalando.orderengine.ruleengineclient

import de.zalando.orderengine.ruleengine.DynamicRules
import de.zalando.orderengine.ruleengine.ExcelBackedRulesetHolder
import de.zalando.orderengine.ruleengine.RuleEngine
import de.zalando.orderengine.ruleengine.Sample
// final def plugins = [:] // ['hello':new ExamplePlugin()]


// main
def engine

try {

    final def itemId = "item1"
    final def itemCount = 10

    final def ruleHolder = new ExcelBackedRulesetHolder("disposition-ruleset.xls", 0);

    engine = new RuleEngine(new DynamicRules(ruleHolder));

    final Sample sample = ['itemID': itemId, 'itemCount': itemCount]

    final def destination = engine.findLowestCostCandidate(sample);

    if(!destination) {
        println "No suitable location found for item '${itemId}'."
    }
    else {
        println "Best destination for item '${itemId}' is stock '${destination}'."
    }
}
catch (Exception e) {
    if(!engine)
        System.out.println("Creating RuleEngine failed: ${e?.getMessage()}");
    else
        System.out.println(engine.wrapError(e));
}
