package com.bilkent.devinsight.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;

@Data
public class CodeReviewEfficiencyMetrics {

    @NotNull
    private Duration averageTimeToMerge;

    @NotNull
    private Double averagePRSize;

    @NotNull
    private Double reviewCoverage;
}
