package com.bilkent.devinsight.dto.request.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReqLogin {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
