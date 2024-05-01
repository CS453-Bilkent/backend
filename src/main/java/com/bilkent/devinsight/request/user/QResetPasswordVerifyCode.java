package com.bilkent.devinsight.request.user;


import lombok.Data;

@Data
public class QResetPasswordVerifyCode {

    private String email;
    private String verifyCode;
}
