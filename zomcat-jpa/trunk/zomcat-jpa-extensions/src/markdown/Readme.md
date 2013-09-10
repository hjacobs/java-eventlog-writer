# Spring-Data-JPA-Extension

### Build and Code

[![Build Status](https://ci-dev.zalando.net/view/All/job/Spring-Data-Jpa-Extensions-UT/badge/icon)](https://ci-dev.zalando.net/view/All/job/Spring-Data-Jpa-Extensions-UT/)

The code is in [SVN] (https://svn.zalando.net/reboot-libs/zomcat-jpa/trunk).

***

### What it does

Brings generated BusinessKeys to your Entities. It's easy to use. Just mark the field with the BusinessKey-Annotation:

    @BusinessKey("BusinessKeyProduct")
    private String businessKey;

An complete Example can be found [here] (https://svn.zalando.net/reboot-libs/zomcat-jpa/trunk/spring-data-jpa-extensions/src/test/java/de/zalando/data/jpa/domain/sample/AnnotatedProduct.java).

***

### POM Declaration

    <dependency>
        <groupId>de.zalando</groupId>
        <artifactId>spring-data-jpa-extensions</artifactId>
        <version>1.4.0-SNAPSHOT</version>
    </dependency>

***

### Configuration

In your Spring-Bean-Definition activate the extension by using the Zalando-JPA-Namespace and enable BusinessKey-Handling with the
`zjpa:businesskey` - Element in the configuration file. The `businesskey-generator-ref` has to reference the `BusinessKeyGenerator` - Bean-Definitinon.

    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:jpa="http://www.springframework.org/schema/data/jpa"
        xmlns:zjpa="http://www.zalando.de/schema/data/jpa" xmlns:context="http://www.springframework.org/schema/context"
        xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/data/jpa http://www.springframework.org/schema/data/jpa/spring-jpa.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
        http://www.zalando.de/schema/data/jpa http://www.zalando.de/schema/data/jpa/spring-zjpa-1.0.xsd">
        
        <zjpa:businesskey businesskey-generator-ref="businessKeyGeneratorStub"/>
        
        <bean id="businessKeyGeneratorStub" class="de.zalando.data.jpa.domain.sample.BusinessKeyGeneratorStub"/>
        
        <jpa:repositories base-package="de.zalando.data.jpa.repository.sample"/>
    </beans>

In the `META-INF/orm.xml` configure the `BusinessKeyEntityListener`:

    <?xml version="1.0" encoding="UTF-8"?>
    <entity-mappings xmlns="http://java.sun.com/xml/ns/persistence/orm"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xsi:schemaLocation="http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm_2_0.xsd"
        version="2.0">
        <persistence-unit-metadata>
            <persistence-unit-defaults>
                <entity-listeners>
                    <entity-listener class="de.zalando.data.jpa.domain.support.BusinessKeyEntityListener"/>
                </entity-listeners>
            </persistence-unit-defaults>
        </persistence-unit-metadata>
    </entity-mappings>
