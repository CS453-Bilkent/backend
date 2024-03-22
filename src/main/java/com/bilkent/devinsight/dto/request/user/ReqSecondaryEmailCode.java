package com.bilkent.devinsight.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqSecondaryEmailCode {

    @NotNull
    private int code;

}
