package de.zalando.zomcat.flowid;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;

@Scope("flow")
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FlowScoped { }
