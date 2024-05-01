package com.bilkent.devinsight.response;

import com.bilkent.devinsight.entity.Contributor;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RDeveloperEffectiveness {

    @NotNull
    private Contributor contributor;

    @NotNull
    private Long commitCount;

    @NotNull
    private Long closedIssuesCount;

    @NotNull
    private Long codeStickiness;

    @NotNull
    private Long bugsFixed;
}
