package com.bilkent.devinsight.response;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ResUserToken {

    private RUser user;
    private String accessToken;
    private String refreshToken;

}
