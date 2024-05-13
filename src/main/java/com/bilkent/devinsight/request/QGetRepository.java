package com.bilkent.devinsight.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QGetRepository {

    @NotNull
    private final String repoName;

    @NotNull
    private final String repoOwner;
}
