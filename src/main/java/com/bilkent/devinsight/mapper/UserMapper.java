package com.bilkent.devinsight.mapper;

import com.bilkent.devinsight.response.RUser;
import com.bilkent.devinsight.entity.User;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class UserMapper {
    public static RUser toDTO(User user) {
        return RUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .picture(user.getPicture())
                .name(user.getName())
                .username(user.getUsername())
                .build();
    }

    public static List<RUser> toDTO(List<User> users) {
        List<RUser> rUsers = new ArrayList<>();

        for (User user : users) {
            rUsers.add(toDTO(user));
        }

        return rUsers;
    }
}
