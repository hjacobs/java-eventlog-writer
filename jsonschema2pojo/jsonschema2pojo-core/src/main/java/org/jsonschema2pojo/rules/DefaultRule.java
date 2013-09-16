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

import static org.apache.commons.lang.StringUtils.isNotEmpty;

import java.text.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import org.jsonschema2pojo.Schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JType;

/**
 * Applies the "enum" schema rule.
 *
 * @see  <a href="http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.20">
 *       http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.20</a>
 */
public class DefaultRule implements Rule<JFieldVar, JFieldVar> {

    private final RuleFactory ruleFactory;

    public DefaultRule(final RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
    }

    /**
     * Applies this schema rule to take the required code generation steps.
     *
     * <p/>Default values are implemented by assigning an expression to the given field (so when instances of the
     * generated POJO are created, its fields will then contain their default values).
     *
     * <p/>Collections (Lists and Sets) are initialized to an empty collection, even when no default value is present in
     * the schema (node is null).
     *
     * @param   nodeName  the name of the property which has (or may have) a default
     * @param   node      the default node (may be null if no default node was present for this property)
     * @param   field     the Java field that has added to a generated type to represent this property
     *
     * @return  field, which will have an init expression is appropriate
     */
    @Override
    public JFieldVar apply(final String nodeName, final JsonNode node, final JFieldVar field,
            final Schema currentSchema) {

        boolean defaultPresent = node != null && isNotEmpty(node.asText());

        String fieldType = field.type().fullName();

        if (fieldType.startsWith(List.class.getName())) {
            field.init(getDefaultList(field.type(), node));

        } else if (fieldType.startsWith(Set.class.getName())) {
            field.init(getDefaultSet(field.type(), node));

        } else if (defaultPresent) {
            field.init(getDefaultValue(field.type(), node));

        }

        return field;
    }

    private JExpression getDefaultValue(JType fieldType, final JsonNode node) {

        if (!fieldType.isPrimitive() && node.isNull()) {
            return JExpr._null();
        }

        fieldType = fieldType.unboxify();

        if (fieldType.fullName().equals(String.class.getName())) {
            return JExpr.lit(node.asText());

        } else if (fieldType.fullName().equals(int.class.getName())) {
            return JExpr.lit(Integer.parseInt(node.asText()));

        } else if (fieldType.fullName().equals(double.class.getName())) {
            return JExpr.lit(Double.parseDouble(node.asText()));

        } else if (fieldType.fullName().equals(boolean.class.getName())) {
            return JExpr.lit(Boolean.parseBoolean(node.asText()));

        } else if (fieldType.fullName().equals(getDateType().getName())) {
            long millisecs = parseDateToMillisecs(node.asText());

            JInvocation newDate = JExpr._new(fieldType);
            newDate.arg(JExpr.lit(millisecs));

            return newDate;

        } else if (fieldType.fullName().equals(long.class.getName())) {
            return JExpr.lit(Long.parseLong(node.asText()));

        } else if (fieldType instanceof JDefinedClass
                && ((JDefinedClass) fieldType).getClassType().equals(ClassType.ENUM)) {

            return getDefaultEnum(fieldType, node);

        } else {
            return JExpr._null();

        }

    }

    private Class<?> getDateType() {
        return ruleFactory.getGenerationConfig().isUseJodaDates() ? DateTime.class : Date.class;
    }

    /**
     * Creates a default value for a list property by:
     *
     * <ol>
     *   <li>Creating a new {@link ArrayList} with the correct generic type</li>
     *   <li>Using {@link Arrays#asList(Object...)} to initialize the list with</li> the correct default values
     * </ol>
     *
     * @param   fieldType  the java type that applies for this field ({@link List} with some generic type argument)
     * @param   node       the node containing default values for this list
     *
     * @return  an expression that creates a default value that can be assigned to this field
     */
    private JExpression getDefaultList(final JType fieldType, final JsonNode node) {

        JClass listGenericType = ((JClass) fieldType).getTypeParameters().get(0);

        JClass listImplClass = fieldType.owner().ref(ArrayList.class);
        listImplClass = listImplClass.narrow(listGenericType);

        JInvocation newListImpl = JExpr._new(listImplClass);

        if (node instanceof ArrayNode && node.size() > 0) {
            JInvocation invokeAsList = fieldType.owner().ref(Arrays.class).staticInvoke("asList");
            for (JsonNode defaultValue : node) {
                invokeAsList.arg(getDefaultValue(listGenericType, defaultValue));
            }

            newListImpl.arg(invokeAsList);
        }

        return newListImpl;

    }

    /**
     * Creates a default value for a set property by:
     *
     * <ol>
     *   <li>Creating a new {@link HashSet} with the correct generic type</li>
     *   <li>Using {@link Arrays#asList(Object...)} to initialize the set with the</li> correct default values
     * </ol>
     *
     * @param   fieldType  the java type that applies for this field ({@link Set} with some generic type argument)
     * @param   node       the node containing default values for this set
     *
     * @return  an expression that creates a default value that can be assigned to this field
     */
    private JExpression getDefaultSet(final JType fieldType, final JsonNode node) {

        JClass setGenericType = ((JClass) fieldType).getTypeParameters().get(0);

        JClass setImplClass = fieldType.owner().ref(HashSet.class);
        setImplClass = setImplClass.narrow(setGenericType);

        JInvocation newSetImpl = JExpr._new(setImplClass);

        if (node instanceof ArrayNode) {
            JInvocation invokeAsList = fieldType.owner().ref(Arrays.class).staticInvoke("asList");
            for (JsonNode defaultValue : node) {
                invokeAsList.arg(getDefaultValue(setGenericType, defaultValue));
            }

            newSetImpl.arg(invokeAsList);
        }

        return newSetImpl;

    }

    private JExpression getDefaultEnum(final JType fieldType, final JsonNode node) {

        JInvocation invokeFromValue = ((JClass) fieldType).staticInvoke("fromValue");
        invokeFromValue.arg(node.asText());

        return invokeFromValue;

    }

    private long parseDateToMillisecs(final String valueAsText) {

        try {
            return Long.parseLong(valueAsText);
        } catch (NumberFormatException nfe) {
            try {
                return new StdDateFormat().parse(valueAsText).getTime();
            } catch (ParseException pe) {
                throw new IllegalArgumentException("Unable to parse this string as a date: " + valueAsText);
            }
        }

    }

}
