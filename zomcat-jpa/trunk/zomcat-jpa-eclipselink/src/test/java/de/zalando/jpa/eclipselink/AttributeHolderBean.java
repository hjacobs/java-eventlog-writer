package de.zalando.jpa.eclipselink;

import javax.persistence.Column;

/**
 * Just for Testing.
 *
 * @author  jbellmann
 */
public class AttributeHolderBean {

    private boolean ordered;

    @Column(name = "is_annotated")
    private boolean annotated;

    private Status orderStatus;

    private String fieldWithoutAnnotation;

    @Column(name = "field_with_annotation")
    private String fieldwithannotation;

    private String brandCode;

}
