package com.bilkent.devinsight.request.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class QChangePassword {

    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
}
