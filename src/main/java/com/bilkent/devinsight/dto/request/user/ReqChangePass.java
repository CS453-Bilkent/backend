package com.bilkent.devinsight.dto.request.user;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReqChangePass {

    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
}
