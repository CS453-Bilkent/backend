package com.bilkent.devinsight.response;


import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class RCommitFrequency {
    private Map<String, Long> dayFrequency;
    private Map<String, Long> timeFrequency;
}