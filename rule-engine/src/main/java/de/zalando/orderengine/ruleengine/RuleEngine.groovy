package de.zalando.orderengine.ruleengine


class RuleEngine {

    def computationTime = 0L
    DynamicRules dynamicRules

    RuleEngine(DynamicRules dynamicRules) {
        this.dynamicRules = dynamicRules
    }

    def findLowestCostCandidate (sample) {
        def t = System.currentTimeMillis()
        final def winner = evaluate(sample)?.min{it.value}
        computationTime = System.currentTimeMillis() - t
        return winner?.key
    }

    def findHighestValueCandidate (sample) {
        def t = System.currentTimeMillis()
        final def winner = evaluate(sample)?.max{it.value}
        computationTime = System.currentTimeMillis() - t
        return winner?.key
    }

    private def evaluate(sample) {

        dynamicRules.setVar("re", dynamicRules);

        final def results=[:]

        dynamicRules.candidates().each {

            sample['candidate'] = it

            dynamicRules.setVar("ctx", sample)

            results[it] = dynamicRules.evaluate()

            println "${it}:${results[it]}"
        }
        return results
    }












    def wrapError(e) {
        final def m = ''<<''  // only for error reporting
        m << "========================== ERROR REPORT ==========================\n"
        m << "class ExcelSheetEnhanced${DynamicRules.class.getSimpleName()} extends ${DynamicRules.class.name} {\n"
        if(e)
            m << "\n// Code caused a problem:\n// ${e.getMessage()}\n\n"
        else
            m << "\n// Code caused a problem.\n\n"
        m << dynamicRules?.getRulesetHolder()?.extensions
        m << "}\n"
        m << "=================================================================="
        return m.toString()
    }
}
