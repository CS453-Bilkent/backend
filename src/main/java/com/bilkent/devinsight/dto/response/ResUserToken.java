package com.bilkent.devinsight.dto.response;

import com.bilkent.devinsight.dto.UserDto;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class ResUserToken {

    private UserDto user;
    private String accessToken;
    private String refreshToken;

}
