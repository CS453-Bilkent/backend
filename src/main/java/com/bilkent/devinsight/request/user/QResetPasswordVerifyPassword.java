package com.bilkent.devinsight.request.user;


import lombok.Data;

@Data
public class QResetPasswordVerifyPassword {

    private String email;
    private String verifyCode;
    private String newPassword;
}
