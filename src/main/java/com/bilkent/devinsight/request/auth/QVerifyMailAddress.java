package com.bilkent.devinsight.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QVerifyMailAddress {

    @NotNull
    private String email;

    @NotNull
    private String code;

}
