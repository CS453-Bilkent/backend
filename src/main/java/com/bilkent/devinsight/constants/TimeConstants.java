package com.bilkent.devinsight.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeConstants {

    public static final int NUMBER_OF_DAYS_IN_A_WEEK = 7;
    public static final int SECOND_IN_MS = 1000;
    public static final int MINUTE_IN_SECONDS = 60;

    public static final int PASSWORD_CHANGE_TOKEN_EXPIRATION_TIME_IN_MINUTES = 15;
    public static final int PASSWORD_RESET_TOKEN_EXPIRATION_TIME_IN_MINUTES = 15;
    public static final int CHANGE_EMAIL_TOKEN_EXPIRATION_TIME_IN_MINUTES = 15;
    public static final int VERIFY_MAIL_ADDRESS_TOKEN_EXPIRATION_TIME_IN_MINUTES = 15;
}
