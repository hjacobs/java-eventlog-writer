package de.zalando.zomcat.flowid;

import org.springframework.stereotype.Component;

@Component
@FlowScoped
public class CounterService {

    private int counter = 0;

    public void inc() {
        counter++;
    }

    public int getCounter() {
        return counter;
    }
}
