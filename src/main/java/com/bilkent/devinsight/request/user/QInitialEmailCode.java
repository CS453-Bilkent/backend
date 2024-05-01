package com.bilkent.devinsight.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QInitialEmailCode {

    @NotNull
    private int code;

}
