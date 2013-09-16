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

/**
 * Factory object for creating {@link Annotator}s for all the supported annotation styles.
 */
public class AnnotatorFactory {

    /**
     * Create a new {@link Annotator} that can create annotations according to the given style.
     *
     * @param  style  the annotation style that dictates what kind of annotations are required.
     */
    public Annotator getAnnotator(final AnnotationStyle style) {

        switch (style) {

            case JACKSON :
            case JACKSON2 :
                return new Jackson2Annotator();

            case JACKSON1 :
                return new Jackson1Annotator();

            case GSON :
                return new GsonAnnotator();

            case NONE :
                return new NoopAnnotator();

            default :
                throw new IllegalArgumentException("Unrecognised annotation style: " + style);
        }

    }

    /**
     * Create a new custom {@link Annotator} from the given class.
     *
     * @param  clazz  A class implementing {@link Annotator}.
     */
    public Annotator getAnnotator(final Class<? extends Annotator> clazz) {

        if (!Annotator.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException("The class name given as a custom annotator (" + clazz.getName()
                    + ") does not refer to a class that implements " + Annotator.class.getName());
        }

        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(
                "Failed to create a custom annotator from the given class. An exception was thrown on trying to create a new instance.",
                e.getCause());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                "Failed to create a custom annotator from the given class. It appears that we do not have access to this class - is both the class and its no-arg constructor marked public?",
                e);
        }

    }

}
