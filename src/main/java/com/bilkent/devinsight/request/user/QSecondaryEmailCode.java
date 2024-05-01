package com.bilkent.devinsight.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QSecondaryEmailCode {

    @NotNull
    private int code;

}
