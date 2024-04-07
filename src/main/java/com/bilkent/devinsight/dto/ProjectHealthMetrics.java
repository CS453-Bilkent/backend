package com.bilkent.devinsight.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectHealthMetrics {

    @NotNull
    private Long averageIssueResolutionTime;

    @NotNull
    private Double closedToOpenIssueRatio;
}
