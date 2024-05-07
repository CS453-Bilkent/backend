package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.request.user.QChangeEmail;
import com.bilkent.devinsight.request.user.QInitialEmailCode;
import com.bilkent.devinsight.request.user.QSecondaryEmailCode;
import com.bilkent.devinsight.entity.enums.UserRole;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

//    @RequiredRole(value = {UserRole.REGISTERED_USER, UserRole.ADMIN})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="change-email/request")
    public ResponseEntity<ApiResponse<Void>> sendChangeEmailCode(@Valid @RequestBody QChangeEmail qChangeEmail) {
        userService.sendChangeEmailCode(qChangeEmail);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Email change code sent to your email successfully")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="change-email/verify-initial")
    public ResponseEntity<ApiResponse<Void>> verifyInitialChangeEmailCode(@Valid @RequestBody QInitialEmailCode
                                                                                  qInitialEmailCode) {
        userService.verifyInitialChangeEmailCode(qInitialEmailCode);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Your code was verified successfully")
                        .build());
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="change-email/verify-secondary")
    public ResponseEntity<ApiResponse<Void>> verifySecondaryChangeEmailCode(@Valid @RequestBody QSecondaryEmailCode
                                                                                    qSecondaryEmailCode) {
        userService.verifySecondaryChangeEmailCode(qSecondaryEmailCode);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Your email was changed successfully")
                        .build());
    }




}