package com.bilkent.devinsight.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Duration;

@Data
public class RCodeReviewEfficiencyMetrics {

    @NotNull
    private Duration averageTimeToMerge;

    @NotNull
    private Double numberOfCommentsPerPR;

    @NotNull
    private Double averagePRSize;

    @NotNull
    private Double reviewCoverage;
}
