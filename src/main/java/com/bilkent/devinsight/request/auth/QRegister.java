package com.bilkent.devinsight.request.auth;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QRegister {

    @NotNull
    private String name;

    @NotNull
    private String email;

    @NotNull
    private String username;

    @NotNull
    private String password;
}
