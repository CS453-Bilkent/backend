package com.bilkent.devinsight.dto.request.user;


import lombok.Data;

@Data
public class ReqResetPassVerifyCode {

    private String email;
    private String verifyCode;
}
