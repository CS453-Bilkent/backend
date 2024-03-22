package com.bilkent.devinsight.constants;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MailConstants {


    public static final int BATCH_SIZE = 5;
    public static final int BATCH_SLEEP_IN_MS = (TimeConstants.MINUTE_IN_SECONDS + 1) * TimeConstants.SECOND_IN_MS;


}
