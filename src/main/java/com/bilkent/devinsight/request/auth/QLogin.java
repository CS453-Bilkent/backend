package com.bilkent.devinsight.request.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QLogin {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;
}
