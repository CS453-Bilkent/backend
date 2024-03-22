package com.bilkent.devinsight.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ForgotPasswordEmailDto {

    private String name;
    private String email;
    private int code;

}
