package com.bilkent.devinsight.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeMailAddressEmailDto {

    private String name;
    private String email;
    private String newEmail;
    private int code;

}
