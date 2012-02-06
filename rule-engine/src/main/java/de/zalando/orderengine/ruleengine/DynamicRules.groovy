package de.zalando.orderengine.ruleengine

import groovy.lang.GroovyShell



class DynamicRules {

    final def binding;
    final def rulesetHolder
    final def resume
    final def gshell
    final def plugins

    DynamicRules(rulesetHolder) {
        this(rulesetHolder, null, true)
    }
    DynamicRules(rulesetHolder, plugins) {
        this(rulesetHolder, plugins, true)
    }

    DynamicRules(rulesetHolder, plugins, resume) {
        this.binding = new Binding()
        this.rulesetHolder = rulesetHolder
        this.resume = resume
        this.gshell = new GroovyShell(binding);
        this.plugins = plugins
    }

    def setVar( name, value) {
        binding.setVariable(name, value);
    }

    def cell (sheetNo, cell) {
        return rulesetHolder.readCell(sheetNo, cell)
    }

    def methodMissing(String methodName, args) {
        try {
            Plugin p = plugins?."${methodName}"
            if(p)
                return p.call(args)
        } catch (Throwable e) {
            println( "No suitable plugin found.")
        }
        try {
            return gshell.evaluate(rulesetHolder.get(methodName))
        } catch(Throwable t) {

            if (resume) {
                println( "Error: ${t} -- re.${methodName}(${args})")
            }
            else
                throw new RuntimeException( "Error: ${t} -- re.${methodName}(${args})")
        }
        return null
    }
}
