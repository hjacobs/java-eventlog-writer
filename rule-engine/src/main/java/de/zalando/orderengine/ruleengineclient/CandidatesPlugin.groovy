package de.zalando.orderengine.ruleengineclient

import de.zalando.orderengine.ruleengine.Plugin


class CandidatesPlugin implements Plugin {

    public Object call(Object... args) {
        return ['a', 'b', 'c']
    }
}
