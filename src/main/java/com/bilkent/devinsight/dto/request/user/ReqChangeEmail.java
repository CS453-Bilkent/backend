package com.bilkent.devinsight.dto.request.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReqChangeEmail {

    @NotNull
    private String newEmail;

}
