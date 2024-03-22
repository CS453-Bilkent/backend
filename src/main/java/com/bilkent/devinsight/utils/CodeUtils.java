package com.bilkent.devinsight.utils;

import com.bilkent.devinsight.constants.CodeConstants;
import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class CodeUtils {

    // Make sure that the code is 6 digits long
    // First digit should not be 0
    public static int generateResetPasswordCode() {
        int codeLength = CodeConstants.RESET_EMAIL_TOKEN_LENGTH;
        return generateCode(codeLength);
    }

    // Make sure that the code is 6 digits long
    // First digit should not be 0
    public static int generateForgotPasswordCode() {
        int codeLength = CodeConstants.FORGOT_EMAIL_TOKEN_LENGTH;
        return generateCode(codeLength);
    }

    public static int generateVerifyEmailCode() {
        int codeLength = CodeConstants.VERIFY_EMAIL_TOKEN_LENGTH;
        return generateCode(codeLength);
    }

    public static int generateChangeEmailCode() {
        int codeLength = CodeConstants.CHANGE_EMAIL_TOKEN_LENGTH;
        return generateCode(codeLength);
    }

    public static String generateNewRoomCode() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private static int generateCode(int codeLength) {
        return (int) (Math.random() * 9 * Math.pow(10, codeLength - 1) + Math.pow(10, codeLength - 1));
    }



}
