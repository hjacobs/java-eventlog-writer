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

import static org.hamcrest.MatcherAssert.assertThat;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Schema;

import org.junit.Before;
import org.junit.Test;

import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import com.sun.codemodel.JClass;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;

public class TypeRuleTest {

    private GenerationConfig config = mock(GenerationConfig.class);
    private RuleFactory ruleFactory = mock(RuleFactory.class);

    private TypeRule rule = new TypeRule(ruleFactory);

    @Before
    public void wireUpConfig() {
        when(ruleFactory.getGenerationConfig()).thenReturn(config);
    }

    @Test
    public void applyGeneratesString() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "string");

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is(String.class.getName()));
    }

    @Test
    public void applyGeneratesDate() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "string");

        TextNode formatNode = TextNode.valueOf("date-time");
        objectNode.put("format", formatNode);

        JType mockDateType = mock(JType.class);
        FormatRule mockFormatRule = mock(FormatRule.class);
        when(mockFormatRule.apply(eq("fooBar"), eq(formatNode), Mockito.isA(JType.class), isNull(Schema.class)))
            .thenReturn(mockDateType);
        when(ruleFactory.getFormatRule()).thenReturn(mockFormatRule);

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result, equalTo(mockDateType));
    }

    @Test
    public void applyGeneratesInteger() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is(Integer.class.getName()));
    }

    @Test
    public void applyGeneratesIntegerPrimitive() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "integer");

        when(config.isUsePrimitives()).thenReturn(true);

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is("int"));
    }

    @Test
    public void applyGeneratesNumber() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is(Double.class.getName()));
    }

    @Test
    public void applyGeneratesNumberPrimitive() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "number");

        when(config.isUsePrimitives()).thenReturn(true);

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is("double"));
    }

    @Test
    public void applyGeneratesBoolean() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "boolean");

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is(Boolean.class.getName()));
    }

    @Test
    public void applyGeneratesBooleanPrimitive() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "boolean");

        when(config.isUsePrimitives()).thenReturn(true);

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is("boolean"));
    }

    @Test
    public void applyGeneratesAnyAsObject() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "any");

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is(Object.class.getName()));
    }

    @Test
    public void applyGeneratesNullAsObject() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "null");

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is(Object.class.getName()));
    }

    @Test
    public void applyGeneratesArray() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "array");

        JClass mockArrayType = mock(JClass.class);
        ArrayRule mockArrayRule = mock(ArrayRule.class);
        when(mockArrayRule.apply("fooBar", objectNode, jpackage, null)).thenReturn(mockArrayType);
        when(ruleFactory.getArrayRule()).thenReturn(mockArrayRule);

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result, is((JType) mockArrayType));
    }

    @Test
    public void applyGeneratesCustomObject() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "object");

        JDefinedClass mockObjectType = mock(JDefinedClass.class);
        ObjectRule mockObjectRule = mock(ObjectRule.class);
        when(mockObjectRule.apply("fooBar", objectNode, jpackage, null)).thenReturn(mockObjectType);
        when(ruleFactory.getObjectRule()).thenReturn(mockObjectRule);

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result, is((JType) mockObjectType));
    }

    @Test
    public void applyChoosesObjectOnUnrecognizedType() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();
        objectNode.put("type", "unknown");

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is(Object.class.getName()));

    }

    @Test
    public void applyDefaultsToTypeAnyObject() {

        JPackage jpackage = new JCodeModel()._package(getClass().getPackage().getName());

        ObjectNode objectNode = new ObjectMapper().createObjectNode();

        JType result = rule.apply("fooBar", objectNode, jpackage, null);

        assertThat(result.fullName(), is(Object.class.getName()));
    }

}
