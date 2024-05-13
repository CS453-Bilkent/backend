package com.bilkent.devinsight.response;

import com.bilkent.devinsight.entity.PullRequest;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RCollaborativePRCount {
    private PullRequest pullRequest;
    private int count;
}