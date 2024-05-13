package com.bilkent.devinsight.response;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RFileCommitRank {

    @NotNull
    private UUID fileId;

    @NotEmpty
    private String fileName;

    @NotNull
    private Integer commitCount;
}
