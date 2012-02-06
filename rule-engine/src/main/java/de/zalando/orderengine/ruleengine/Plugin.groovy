package de.zalando.orderengine.ruleengine


interface Plugin {
    def call(Object [] args)
}
