package de.zalando.zomcat.flowid;

import org.springframework.context.annotation.Scope;

import org.springframework.stereotype.Component;

@Component
@Scope("flow")
public class CounterService {

    private int counter = 0;

    public void inc() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }
}
