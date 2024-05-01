package com.bilkent.devinsight.response.email;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class REmailChangeMail {

    private String name;
    private String email;
    private String newEmail;
    private int code;

}
