package com.apsitcoders.bus.model;

/**
 * Created by adityathanekar on 07/10/17.
 */

public class BusStop {
    String key, name;

    public BusStop(String key, String name) {
        this.key = key;
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
