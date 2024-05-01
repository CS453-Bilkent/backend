package com.bilkent.devinsight.request;


import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QGithubScrape {

    @NotNull
    private final String repoName;

    @NotNull
    private final String repoOwner;


}
