package com.bilkent.devinsight.response;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFileCommitRank {

    @NotNull
    private Long fileId;

    @NotEmpty
    private String fileName;

    @NotNull
    private Integer commitCount;
}
