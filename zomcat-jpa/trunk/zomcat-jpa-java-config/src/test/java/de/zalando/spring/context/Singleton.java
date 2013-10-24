package de.zalando.spring.context;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class Singleton {
    public String id = UUID.randomUUID().toString();
}
