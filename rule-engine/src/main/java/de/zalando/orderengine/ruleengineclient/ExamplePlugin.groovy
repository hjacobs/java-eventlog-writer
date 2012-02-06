package de.zalando.orderengine.ruleengineclient

import de.zalando.orderengine.ruleengine.Plugin


class ExamplePlugin implements Plugin {

    public Object call(Object... args) {
        String arg = args?.join("; ")
        return "${this.class.simpleName}.call(${arg})";
    }
}
