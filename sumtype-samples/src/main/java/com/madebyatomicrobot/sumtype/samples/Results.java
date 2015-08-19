package com.madebyatomicrobot.sumtype.samples;

import java.util.List;

public class Results {
    private final List<String> results;

    public Results(List<String> results) {
        this.results = results;
    }

    @Override
    public String toString() {
        return "Results{" +
                "results=" + results +
                '}';
    }
}
