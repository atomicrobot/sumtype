package com.madebyatomicrobot.sumtype.samples;

import com.madebyatomicrobot.sumtype.annotations.SumType;

@SumType
public interface Query {
    Loading loading();
    int progress();

    Results results();
    Error error();

    void networkUnavailable();
}
