/**
 * Copyright © 2010-2013 Nokia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jsonschema2pojo.rules;

import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Schema;

import com.fasterxml.jackson.databind.JsonNode;

import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JType;

/**
 * Applies the "type" schema rule.
 *
 * @see  <a href="http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.1">
 *       http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.1</a>
 */
public class TypeRule implements Rule<JClassContainer, JType> {

    private static final String DEFAULT_TYPE_NAME = "any";

    private final RuleFactory ruleFactory;

    protected TypeRule(final RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
    }

    /**
     * Applies this schema rule to take the required code generation steps.
     *
     * <p/>When applied, this rule reads the details of the given node to determine the appropriate Java type to return.
     * This may be a newly generated type, it may be a primitive type or other type such as {@link java.lang.String} or
     * {@link java.lang.Object}.
     *
     * <p/>JSON schema types and their Java type equivalent:
     *
     * <ul>* "type":"any" => {@link java.lang.Object}* "type":"array" => Either {@link java.util.Set} or*
     *   "type":"boolean" => <code>boolean</code>* "type":"integer" => <code>int</code>* "type":"null" =>
     *   {@link java.lang.Object}* "type":"number" => <code>double</code>* "type":"object" => Generated type (see
     *   {@link ObjectRule}) {@link java.util.List}, see {@link ArrayRule}* "type":"string" => {@link java.lang.String}
     *   (or alternative based on presence of "format", see {@link FormatRule})
     * </ul>
     *
     * @param   nodeName         the name of the node for which this "type" rule applies
     * @param   node             the node for which this "type" rule applies
     * @param   jClassContainer  the package into which any newly generated type may be placed
     *
     * @return  the Java type which, after reading the details of the given schema node, most appropriately matches the
     *          "type" specified
     */
    @Override
    public JType apply(final String nodeName, final JsonNode node, final JClassContainer jClassContainer,
            final Schema schema) {

        String propertyTypeName = getTypeName(node);

        JType type;

        if (propertyTypeName.equals("string")) {

            type = jClassContainer.owner().ref(String.class);
        } else if (propertyTypeName.equals("number")) {

            type = unboxIfNecessary(jClassContainer.owner().ref(Double.class), ruleFactory.getGenerationConfig());
        } else if (propertyTypeName.equals("integer")) {

            JType typeToUseForIntegers = getIntegerType(jClassContainer.owner(), ruleFactory.getGenerationConfig());
            type = unboxIfNecessary(typeToUseForIntegers, ruleFactory.getGenerationConfig());
        } else if (propertyTypeName.equals("boolean")) {

            type = unboxIfNecessary(jClassContainer.owner().ref(Boolean.class), ruleFactory.getGenerationConfig());
        } else if (propertyTypeName.equals("object")) {

            type = ruleFactory.getObjectRule().apply(nodeName, node, jClassContainer.getPackage(), schema);
        } else if (propertyTypeName.equals("array")) {

            type = ruleFactory.getArrayRule().apply(nodeName, node, jClassContainer.getPackage(), schema);
        } else {

            type = jClassContainer.owner().ref(Object.class);
        }

        if (node.has("format")) {
            type = ruleFactory.getFormatRule().apply(nodeName, node.get("format"), type, schema);
        }

        return type;
    }

    private String getTypeName(final JsonNode node) {
        if (node.has("type") && node.get("type").isArray() && node.get("type").size() > 0) {
            return node.get("type").get(0).asText();
        }

        if (node.has("type")) {
            return node.get("type").asText();
        }

        return DEFAULT_TYPE_NAME;
    }

    private JType unboxIfNecessary(final JType type, final GenerationConfig config) {
        if (config.isUsePrimitives()) {
            return type.unboxify();
        } else {
            return type;
        }
    }

    private JType getIntegerType(final JCodeModel owner, final GenerationConfig config) {
        if (config.isUseLongIntegers()) {
            return owner.ref(Long.class);
        } else {
            return owner.ref(Integer.class);
        }
    }

}
