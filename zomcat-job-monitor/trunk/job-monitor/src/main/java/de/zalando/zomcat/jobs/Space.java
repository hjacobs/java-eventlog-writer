package de.zalando.zomcat.jobs;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Space implements Serializable {
    private static final long serialVersionUID = -148434945461732640L;

    String name;
    Map<String, Integer> stateCountMap;

    public Space(final String name) {
        this.name = name;
    }

    public void addStateCount(final String state, final int count) {
        if (stateCountMap == null) {
            stateCountMap = new HashMap<String, Integer>();
        }

        stateCountMap.put(state, count);

    }

    public String getName() {
        return name;
    }

    public int getTotalCount() {
        int total = 0;
        for (final int i : stateCountMap.values()) {
            total += i;
        }

        return total;
    }

    public int getCountForState(final String name) {
        return stateCountMap.get(name);
    }

    public List<String> getStates() {
        return new ArrayList<String>(stateCountMap.keySet());
    }
}
