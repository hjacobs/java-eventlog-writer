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

import static java.util.Arrays.asList;

import static org.apache.commons.lang.StringUtils.capitalize;
import static org.apache.commons.lang.StringUtils.containsOnly;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.join;
import static org.apache.commons.lang.StringUtils.splitByCharacterTypeCamelCase;
import static org.apache.commons.lang.StringUtils.upperCase;

import static org.jsonschema2pojo.rules.PrimitiveTypes.isPrimitive;

import java.util.*;

import javax.annotation.Generated;

import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.SchemaMapper;

import org.jsonschema2pojo.exception.ClassAlreadyExistsException;
import org.jsonschema2pojo.exception.GenerationException;

import com.fasterxml.jackson.databind.JsonNode;

import com.sun.codemodel.ClassType;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JEnumConstant;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JForEach;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * Applies the "enum" schema rule.
 *
 * @see  <a href="http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.19">
 *       http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.19</a>
 */
public class EnumRule implements Rule<JClassContainer, JType> {

    private static final String VALUE_FIELD_NAME = "value";

    private final RuleFactory ruleFactory;

    protected EnumRule(final RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
    }

    /**
     * Applies this schema rule to take the required code generation steps.
     *
     * <p/>A Java {@link Enum} is created, with constants for each of the enum values present in the schema. The enum
     * name is derived from the nodeName, and the enum type itself is created as an inner class of the owning type. In
     * the rare case that no owning type exists (the enum is the root of the schema), then the enum becomes a public
     * class in its own right.
     *
     * <p/>The actual JSON value for each enum constant is held in a property called "value" in the generated type. A
     * static factory method <code>fromValue(String)</code> is added to the generated enum, and the methods are
     * annotated to allow Jackson to marshal/unmarshal values correctly.
     *
     * @param   nodeName   the name of the property which is an "enum"
     * @param   node       the enum node
     * @param   container  the class container (class or package) to which this enum should be added
     *
     * @return  the newly generated Java type that was created to represent the given enum
     */
    @Override
    public JType apply(final String nodeName, final JsonNode node, final JClassContainer container,
            final Schema schema) {

        JDefinedClass uEnum;
        try {
            uEnum = createEnum(node, nodeName, container);
        } catch (ClassAlreadyExistsException e) {
            return e.getExistingClass();
        }

        schema.setJavaTypeIfEmpty(uEnum);

        addGeneratedAnnotation(uEnum);

        JFieldVar valueField = addValueField(uEnum);
        addToString(uEnum, valueField);
        addEnumConstants(node.path("enum"), uEnum);
        addFactoryMethod(node.path("enum"), uEnum);

        return uEnum;
    }

    private JDefinedClass createEnum(final JsonNode node, final String nodeName, final JClassContainer container)
        throws ClassAlreadyExistsException {

        int modifiers = container.isPackage() ? JMod.PUBLIC : JMod.PUBLIC | JMod.STATIC;

        try {
            if (node.has("javaType")) {
                String fqn = node.get("javaType").asText();

                if (isPrimitive(fqn, container.owner())) {
                    throw new GenerationException("Primitive type '" + fqn + "' cannot be used as an enum.");
                }

                try {
                    Class<?> existingClass = Thread.currentThread().getContextClassLoader().loadClass(fqn);
                    throw new ClassAlreadyExistsException(container.owner().ref(existingClass));
                } catch (ClassNotFoundException e) {
                    return container.owner()._class(fqn, ClassType.ENUM);
                }
            } else {
                try {
                    return container._class(modifiers, getEnumName(nodeName), ClassType.ENUM);
                } catch (JClassAlreadyExistsException e) {
                    throw new GenerationException(e);
                }
            }
        } catch (JClassAlreadyExistsException e) {
            throw new ClassAlreadyExistsException(e.getExistingClass());
        }
    }

    private void addFactoryMethod(final JsonNode node, final JDefinedClass uEnum) {
        JFieldVar quickLookupMap = addQuickLookupMap(uEnum);

        JMethod fromValue = uEnum.method(JMod.PUBLIC | JMod.STATIC, uEnum, "fromValue");
        JVar valueParam = fromValue.param(String.class, "value");

        JBlock body = fromValue.body();
        JVar constant = body.decl(uEnum, "constant");
        constant.init(quickLookupMap.invoke("get").arg(valueParam));

        JConditional uIf = body._if(constant.eq(JExpr._null()));

        JInvocation illegalArgumentException = JExpr._new(uEnum.owner().ref(IllegalArgumentException.class));
        illegalArgumentException.arg(valueParam);
        uIf._then()._throw(illegalArgumentException);
        uIf._else()._return(constant);

        ruleFactory.getAnnotator().enumCreatorMethod(fromValue);
    }

    private JFieldVar addQuickLookupMap(final JDefinedClass uEnum) {

        JClass lookupType = uEnum.owner().ref(Map.class).narrow(uEnum.owner().ref(String.class), uEnum);
        JFieldVar lookupMap = uEnum.field(JMod.PRIVATE | JMod.STATIC, lookupType, "constants");

        JClass lookupImplType = uEnum.owner().ref(HashMap.class).narrow(uEnum.owner().ref(String.class), uEnum);
        lookupMap.init(JExpr._new(lookupImplType));

        JForEach forEach = uEnum.init().forEach(uEnum, "c", uEnum.staticInvoke("values"));
        JInvocation put = forEach.body().invoke(lookupMap, "put");
        put.arg(forEach.var().ref("value"));
        put.arg(forEach.var());

        return lookupMap;
    }

    private JFieldVar addValueField(final JDefinedClass uEnum) {
        JFieldVar valueField = uEnum.field(JMod.PRIVATE | JMod.FINAL, String.class, VALUE_FIELD_NAME);

        JMethod constructor = uEnum.constructor(JMod.PRIVATE);
        JVar valueParam = constructor.param(String.class, VALUE_FIELD_NAME);
        JBlock body = constructor.body();
        body.assign(JExpr._this().ref(valueField), valueParam);

        return valueField;
    }

    private void addToString(final JDefinedClass uEnum, final JFieldVar valueField) {
        JMethod toString = uEnum.method(JMod.PUBLIC, String.class, "toString");
        JBlock body = toString.body();

        body._return(JExpr._this().ref(valueField));

        ruleFactory.getAnnotator().enumValueMethod(toString);
        toString.annotate(Override.class);
    }

    private void addEnumConstants(final JsonNode node, final JDefinedClass uEnum) {
        for (Iterator<JsonNode> values = node.elements(); values.hasNext();) {
            JsonNode value = values.next();

            if (!value.isNull()) {
                JEnumConstant constant = uEnum.enumConstant(getConstantName(value.asText()));
                constant.arg(JExpr.lit(value.asText()));
            }
        }
    }

    private void addGeneratedAnnotation(final JDefinedClass jclass) {
        JAnnotationUse generated = jclass.annotate(Generated.class);
        generated.param("value", SchemaMapper.class.getPackage().getName());
    }

    private String getEnumName(final String nodeName) {
        String className = ruleFactory.getNameHelper().replaceIllegalCharacters(capitalize(nodeName));
        return ruleFactory.getNameHelper().normalizeName(className);
    }

    private String getConstantName(final String nodeName) {
        List<String> enumNameGroups = new ArrayList<String>(asList(splitByCharacterTypeCamelCase(nodeName)));

        String enumName = "";
        for (Iterator<String> iter = enumNameGroups.iterator(); iter.hasNext();) {
            if (containsOnly(ruleFactory.getNameHelper().replaceIllegalCharacters(iter.next()), "_")) {
                iter.remove();
            }
        }

        enumName = upperCase(join(enumNameGroups, "_"));

        if (isEmpty(enumName)) {
            enumName = "__EMPTY__";
        } else if (Character.isDigit(enumName.charAt(0))) {
            enumName = "_" + enumName;
        }

        return enumName;
    }

}
