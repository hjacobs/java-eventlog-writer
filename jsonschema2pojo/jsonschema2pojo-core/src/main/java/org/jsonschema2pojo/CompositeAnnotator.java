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

import com.fasterxml.jackson.databind.JsonNode;

import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;

/**
 * An annotator (implementing the composite pattern) that can be used to compose many annotators together.
 */
public class CompositeAnnotator implements Annotator {

    private final Annotator[] annotators;

    /**
     * Create a new composite annotator, made up of a given set of child annotators.
     *
     * @param  annotators  The annotators that will be called whenever this annotator is called. The child annotators
     *                     provided will called in the order that they appear in this argument list.
     */
    public CompositeAnnotator(final Annotator... annotators) {
        this.annotators = annotators;
    }

    @Override
    public void propertyOrder(final JDefinedClass clazz, final JsonNode propertiesNode) {
        for (Annotator annotator : annotators) {
            annotator.propertyOrder(clazz, propertiesNode);
        }
    }

    @Override
    public void propertyInclusion(final JDefinedClass clazz, final JsonNode schema) {
        for (Annotator annotator : annotators) {
            annotator.propertyInclusion(clazz, schema);
        }
    }

    @Override
    public void propertyField(final JFieldVar field, final JDefinedClass clazz, final String propertyName,
            final JsonNode propertyNode) {
        for (Annotator annotator : annotators) {
            annotator.propertyField(field, clazz, propertyName, propertyNode);
        }
    }

    @Override
    public void propertyGetter(final JMethod getter, final String propertyName) {
        for (Annotator annotator : annotators) {
            annotator.propertyGetter(getter, propertyName);
        }
    }

    @Override
    public void propertySetter(final JMethod setter, final String propertyName) {
        for (Annotator annotator : annotators) {
            annotator.propertySetter(setter, propertyName);
        }
    }

    @Override
    public void anyGetter(final JMethod getter) {
        for (Annotator annotator : annotators) {
            annotator.anyGetter(getter);
        }
    }

    @Override
    public void anySetter(final JMethod setter) {
        for (Annotator annotator : annotators) {
            annotator.anySetter(setter);
        }
    }

    @Override
    public void enumCreatorMethod(final JMethod creatorMethod) {
        for (Annotator annotator : annotators) {
            annotator.enumCreatorMethod(creatorMethod);
        }
    }

    @Override
    public void enumValueMethod(final JMethod valueMethod) {
        for (Annotator annotator : annotators) {
            annotator.enumValueMethod(valueMethod);
        }
    }

    @Override
    public boolean isAdditionalPropertiesSupported() {
        for (Annotator annotator : annotators) {
            if (!annotator.isAdditionalPropertiesSupported()) {
                return false;
            }
        }

        return true;
    }

}
