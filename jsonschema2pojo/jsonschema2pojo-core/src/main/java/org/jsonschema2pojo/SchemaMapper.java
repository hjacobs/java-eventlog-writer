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

package org.jsonschema2pojo;

import java.io.IOException;

import java.net.URL;

import org.jsonschema2pojo.rules.RuleFactory;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JPackage;

/**
 * Generates Java types from a JSON schema. Can accept a factory which will be used to create type generation rules for
 * this mapper.
 */
public class SchemaMapper {
    private static final JsonNodeFactory NODE_FACTORY = JsonNodeFactory.instance;

    private final RuleFactory ruleFactory;
    private final SchemaGenerator schemaGenerator;

    /**
     * Create a schema mapper with the given {@link RuleFactory}.
     *
     * @param  ruleFactory      A factory used by this mapper to create Java type generation rules.
     * @param  schemaGenerator  the generator that this mapper will use if the config dictates that the input documents
     *                          are plain json (not json schema)
     */
    public SchemaMapper(final RuleFactory ruleFactory, final SchemaGenerator schemaGenerator) {
        this.ruleFactory = ruleFactory;
        this.schemaGenerator = schemaGenerator;
    }

    /**
     * Create a schema mapper with the default {@link RuleFactory} implementation.
     *
     * @see  RuleFactory
     */
    public SchemaMapper() {
        this(new RuleFactory(), new SchemaGenerator());
    }

    /**
     * Reads a schema and adds generated types to the given code model.
     *
     * @param   codeModel    the java code-generation context that should be used to generated new types
     * @param   className    the name of the parent class the represented by this schema
     * @param   packageName  the target package that should be used for generated types
     * @param   schemaUrl    location of the schema to be used as input
     *
     * @throws  IOException  if the schema content cannot be read
     */
    public void generate(final JCodeModel codeModel, final String className, final String packageName,
            final URL schemaUrl) throws IOException {

        JPackage jpackage = codeModel._package(packageName);

        ObjectNode schemaNode = readSchema(schemaUrl);

        ruleFactory.getSchemaRule().apply(className, schemaNode, jpackage, new Schema(null, schemaNode));

    }

    private ObjectNode readSchema(final URL schemaUrl) {

        switch (ruleFactory.getGenerationConfig().getSourceType()) {

            case JSONSCHEMA :

                ObjectNode schemaNode = NODE_FACTORY.objectNode();
                schemaNode.put("$ref", schemaUrl.toString());
                return schemaNode;

            case JSON :
                return schemaGenerator.schemaFromExample(schemaUrl);

            default :
                throw new IllegalArgumentException("Unrecognised source type: "
                        + ruleFactory.getGenerationConfig().getSourceType());
        }

    }
}
