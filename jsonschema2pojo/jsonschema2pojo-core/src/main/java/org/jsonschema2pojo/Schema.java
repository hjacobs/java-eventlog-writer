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

import java.net.URI;

import com.fasterxml.jackson.databind.JsonNode;

import com.sun.codemodel.JType;

/**
 * A JSON Schema document.
 */
public class Schema {

    private URI id;
    private JsonNode content;
    private JType javaType;

    protected Schema(final URI id, final JsonNode content) {
        this.id = id;
        this.content = content;
    }

    public JType getJavaType() {
        return javaType;
    }

    public void setJavaType(final JType javaType) {
        this.javaType = javaType;
    }

    public void setJavaTypeIfEmpty(final JType javaType) {
        if (this.getJavaType() == null) {
            this.setJavaType(javaType);
        }
    }

    public URI getId() {
        return id;
    }

    public JsonNode getContent() {
        return content;
    }

    public boolean isGenerated() {
        return (javaType != null);
    }

}
