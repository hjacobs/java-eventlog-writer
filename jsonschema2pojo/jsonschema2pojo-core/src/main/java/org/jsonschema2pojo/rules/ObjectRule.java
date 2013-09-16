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

import static org.apache.commons.lang.StringUtils.capitalize;

import static org.jsonschema2pojo.rules.PrimitiveTypes.isPrimitive;
import static org.jsonschema2pojo.rules.PrimitiveTypes.primitiveType;

import java.io.Serializable;

import java.lang.reflect.Modifier;

import javax.annotation.Generated;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import org.jsonschema2pojo.Schema;
import org.jsonschema2pojo.SchemaMapper;

import org.jsonschema2pojo.exception.ClassAlreadyExistsException;

import com.fasterxml.jackson.databind.JsonNode;

import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JClass;
import com.sun.codemodel.JClassAlreadyExistsException;
import com.sun.codemodel.JClassContainer;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JInvocation;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;

/**
 * Applies the generation steps required for schemas of type "object".
 *
 * @see  <a href="http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.1">
 *       http://tools.ietf.org/html/draft-zyp-json-schema-03#section-5.1</a>
 */
public class ObjectRule implements Rule<JPackage, JType> {

    private final RuleFactory ruleFactory;

    protected ObjectRule(final RuleFactory ruleFactory) {
        this.ruleFactory = ruleFactory;
    }

    /**
     * Applies this schema rule to take the required code generation steps.
     *
     * <p/>When this rule is applied for schemas of type object, the properties of the schema are used to generate a new
     * Java class and determine its characteristics. See other implementers of {@link Rule} for details.
     *
     * <p/>A new Java type will be created when this rule is applied, it is annotated as {@link Generated}, it is given
     * <code>equals</code>, <code>hashCode</code> and <code>toString</code> methods and implements {@link Serializable}.
     */
    @Override
    public JType apply(final String nodeName, final JsonNode node, final JPackage uPackage, final Schema schema) {

        JType superType = getSuperType(nodeName, node, uPackage, schema);

        if (superType.isPrimitive() || isFinal(superType)) {
            return superType;
        }

        JDefinedClass jclass;
        try {
            jclass = createClass(nodeName, node, uPackage);
        } catch (ClassAlreadyExistsException e) {
            return e.getExistingClass();
        }

        jclass._extends((JClass) superType);

        schema.setJavaTypeIfEmpty(jclass);
        addGeneratedAnnotation(jclass);

        if (node.has("title")) {
            ruleFactory.getTitleRule().apply(nodeName, node.get("title"), jclass, schema);
        }

        if (node.has("description")) {
            ruleFactory.getDescriptionRule().apply(nodeName, node.get("description"), jclass, schema);
        }

        if (node.has("properties")) {
            ruleFactory.getPropertiesRule().apply(nodeName, node.get("properties"), jclass, schema);
        }

        if (ruleFactory.getGenerationConfig().isIncludeToString()) {
            addToString(jclass);
        }

        if (ruleFactory.getGenerationConfig().isIncludeHashcodeAndEquals()) {
            addHashCode(jclass);
            addEquals(jclass);
        }

        if (node.has("javaInterfaces")) {
            addInterfaces(jclass, node.get("javaInterfaces"));
        }

        ruleFactory.getAdditionalPropertiesRule().apply(nodeName, node.get("additionalProperties"), jclass, schema);

        return jclass;

    }

    /**
     * Creates a new Java class that will be generated.
     *
     * @param   nodeName  the node name which may be used to dictate the new class name
     * @param   node      the node representing the schema that caused the need for a new class. This node may include a
     *                    'javaType' property which if present will override the fully qualified name of the newly
     *                    generated class.
     * @param   uPackage  the package which may contain a new class after this method call
     *
     * @return  a reference to a newly created class
     *
     * @throws  ClassAlreadyExistsException  if the given arguments cause an attempt to create a class that already
     *                                       exists, either on the classpath or in the current map of classes to be
     *                                       generated.
     */
    private JDefinedClass createClass(final String nodeName, final JsonNode node, final JPackage uPackage)
        throws ClassAlreadyExistsException {

        JDefinedClass newType;

        try {
            if (node.has("javaType")) {
                String fqn = node.get("javaType").asText();

                if (isPrimitive(fqn, uPackage.owner())) {
                    throw new ClassAlreadyExistsException(primitiveType(fqn, uPackage.owner()));
                }

                try {
                    Class<?> existingClass = Thread.currentThread().getContextClassLoader().loadClass(fqn);
                    throw new ClassAlreadyExistsException(uPackage.owner().ref(existingClass));
                } catch (ClassNotFoundException e) {
                    newType = uPackage.owner()._class(fqn);
                }
            } else {
                newType = uPackage._class(getClassName(nodeName, uPackage));
            }
        } catch (JClassAlreadyExistsException e) {
            throw new ClassAlreadyExistsException(e.getExistingClass());
        }

        ruleFactory.getAnnotator().propertyInclusion(newType, node);

        return newType;

    }

    private boolean isFinal(final JType superType) {
        try {
            Class<?> javaClass = Class.forName(superType.fullName());
            return Modifier.isFinal(javaClass.getModifiers());
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private JType getSuperType(final String nodeName, final JsonNode node, final JClassContainer jClassContainer,
            final Schema schema) {
        JType superType = jClassContainer.owner().ref(Object.class);
        if (node.has("extends")) {
            superType = ruleFactory.getSchemaRule().apply(nodeName + "Parent", node.get("extends"), jClassContainer,
                    schema);
        }

        return superType;
    }

    private void addGeneratedAnnotation(final JDefinedClass jclass) {
        JAnnotationUse generated = jclass.annotate(Generated.class);
        generated.param("value", SchemaMapper.class.getPackage().getName());
    }

    private void addToString(final JDefinedClass jclass) {
        JMethod toString = jclass.method(JMod.PUBLIC, String.class, "toString");

        JBlock body = toString.body();
        JInvocation reflectionToString = jclass.owner().ref(ToStringBuilder.class).staticInvoke("reflectionToString");
        reflectionToString.arg(JExpr._this());
        body._return(reflectionToString);

        toString.annotate(Override.class);
    }

    private void addHashCode(final JDefinedClass jclass) {
        JMethod hashCode = jclass.method(JMod.PUBLIC, int.class, "hashCode");

        JBlock body = hashCode.body();
        JInvocation reflectionHashCode = jclass.owner().ref(HashCodeBuilder.class).staticInvoke("reflectionHashCode");
        reflectionHashCode.arg(JExpr._this());
        body._return(reflectionHashCode);

        hashCode.annotate(Override.class);
    }

    private void addEquals(final JDefinedClass jclass) {
        JMethod equals = jclass.method(JMod.PUBLIC, boolean.class, "equals");
        JVar otherObject = equals.param(Object.class, "other");

        JBlock body = equals.body();
        JInvocation reflectionEquals = jclass.owner().ref(EqualsBuilder.class).staticInvoke("reflectionEquals");
        reflectionEquals.arg(JExpr._this());
        reflectionEquals.arg(otherObject);
        body._return(reflectionEquals);

        equals.annotate(Override.class);
    }

    private void addInterfaces(final JDefinedClass jclass, final JsonNode javaInterfaces) {
        for (JsonNode i : javaInterfaces) {
            jclass._implements(jclass.owner().ref(i.asText()));
        }
    }

    private String getClassName(final String nodeName, final JPackage uPackage) {
        String className = ruleFactory.getNameHelper().replaceIllegalCharacters(capitalize(nodeName));
        String normalizedName = ruleFactory.getNameHelper().normalizeName(className);
        return makeUnique(normalizedName, uPackage);
    }

    private String makeUnique(final String className, final JPackage uPackage) {
        try {
            JDefinedClass uClass = uPackage._class(className);
            uPackage.remove(uClass);
            return className;
        } catch (JClassAlreadyExistsException e) {
            return makeUnique(className + "_", uPackage);
        }
    }

}
