package com.bilkent.devinsight.response.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class REmailResetPassword {

    private String name;
    private String email;
    private int code;

}
