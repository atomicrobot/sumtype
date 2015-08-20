package com.madebyatomicrobot.sumtype.samples;

import com.madebyatomicrobot.sumtype.annotations.SumType;

@SumType
public interface Query extends ProgressAPIQuery, NetworkQuery {
    Results results();

}
