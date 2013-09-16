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

import java.util.Iterator;

import org.jsonschema2pojo.exception.GenerationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonschema.SchemaAware;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.BeanSerializerFactory;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.std.NullSerializer;

public class SchemaGenerator {

    private static final ObjectMapper OBJECT_MAPPER =
        new ObjectMapper().enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);

    public ObjectNode schemaFromExample(final URL example) {

        try {
            JsonNode content = OBJECT_MAPPER.readTree(example);
            return schemaFromExample(content);
        } catch (IOException e) {
            throw new GenerationException("Could not process JSON in source file", e);
        }

    }

    public ObjectNode schemaFromExample(final JsonNode example) {

        if (example.isObject()) {
            return objectSchema(example);
        } else if (example.isArray()) {
            return arraySchema(example);
        } else {
            return simpleTypeSchema(example);
        }

    }

    private ObjectNode objectSchema(final JsonNode exampleObject) {

        ObjectNode schema = OBJECT_MAPPER.createObjectNode();
        schema.put("type", "object");

        ObjectNode properties = OBJECT_MAPPER.createObjectNode();
        for (Iterator<String> iter = exampleObject.fieldNames(); iter.hasNext();) {
            String property = iter.next();
            properties.put(property, schemaFromExample(exampleObject.get(property)));
        }

        schema.put("properties", properties);

        return schema;
    }

    private ObjectNode arraySchema(final JsonNode exampleArray) {
        ObjectNode schema = OBJECT_MAPPER.createObjectNode();

        schema.put("type", "array");

        if (exampleArray.size() > 0) {

            JsonNode exampleItem = exampleArray.get(0).isObject() ? mergeArrayItems(exampleArray) : exampleArray.get(0);

            schema.put("items", schemaFromExample(exampleItem));
        }

        return schema;
    }

    private JsonNode mergeArrayItems(final JsonNode exampleArray) {

        ObjectNode mergedItems = OBJECT_MAPPER.createObjectNode();

        for (JsonNode item : exampleArray) {
            if (item.isObject()) {
                mergedItems.putAll((ObjectNode) item);
            }
        }

        return mergedItems;
    }

    private ObjectNode simpleTypeSchema(final JsonNode exampleValue) {

        try {

            Object valueAsJavaType = OBJECT_MAPPER.treeToValue(exampleValue, Object.class);

            SchemaAware valueSerializer = getValueSerializer(valueAsJavaType);

            return (ObjectNode) valueSerializer.getSchema(OBJECT_MAPPER.getSerializerProvider(), null);
        } catch (JsonMappingException e) {
            throw new GenerationException("Unable to generate a schema for this json example: " + exampleValue, e);
        } catch (JsonProcessingException e) {
            throw new GenerationException("Unable to generate a schema for this json example: " + exampleValue, e);
        }

    }

    private SchemaAware getValueSerializer(final Object valueAsJavaType) throws JsonMappingException {

        SerializerProvider serializerProvider =
            new DefaultSerializerProvider.Impl().createInstance(OBJECT_MAPPER.getSerializationConfig(),
                BeanSerializerFactory.instance);

        if (valueAsJavaType == null) {
            return NullSerializer.instance;
        } else {
            Class<? extends Object> javaTypeForValue = valueAsJavaType.getClass();
            JsonSerializer<Object> valueSerializer = serializerProvider.findValueSerializer(javaTypeForValue, null);
            return (SchemaAware) valueSerializer;
        }
    }

}
