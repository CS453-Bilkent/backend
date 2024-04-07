package com.bilkent.devinsight.dto;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class FileCommitRank {

    @NotNull
    private Long fileId;

    @NotEmpty
    private String fileName;

    @NotNull
    private Integer commitCount;
}
