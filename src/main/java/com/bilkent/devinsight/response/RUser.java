package com.bilkent.devinsight.response;


import com.bilkent.devinsight.entity.enums.UserRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RUser {

    @NotNull
    private UUID id;

    @NotNull
    private String name;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private UserRole role;

    @NotNull
    private String picture;

    public String toString(){
        return "User [id=" + id + ", name=" + name + ", email=" + email + "]";
    }
}
