package com.bilkent.devinsight.response;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RMostCommonlyChangedFiles {

    private List<String> fileName;
    private List<String> filePath;
    private Long changeCount;
}
