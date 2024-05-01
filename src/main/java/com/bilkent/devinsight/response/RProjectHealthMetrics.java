package com.bilkent.devinsight.response;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RProjectHealthMetrics {

    @NotNull
    private Long averageIssueResolutionTime;

    @NotNull
    private Double closedToOpenIssueRatio;
}
