package com.bilkent.devinsight.response;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RScrape {

    @NotNull
    private int success;

    @NotNull
    private int failed;

    @NotNull
    private int total;

}
