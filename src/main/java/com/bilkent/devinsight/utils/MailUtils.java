package com.bilkent.devinsight.utils;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MailUtils {

    public static boolean isValidEmail(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }
}