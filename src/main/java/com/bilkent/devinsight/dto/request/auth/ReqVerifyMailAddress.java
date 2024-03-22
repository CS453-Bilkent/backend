package com.bilkent.devinsight.dto.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqVerifyMailAddress {

    @NotNull
    private String email;

    @NotNull
    private String code;

}
