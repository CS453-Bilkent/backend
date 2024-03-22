package com.bilkent.devinsight.controller;

import com.bilkent.devinsight.annotations.RequiredRole;
import com.bilkent.devinsight.dto.request.user.ReqChangeEmail;
import com.bilkent.devinsight.dto.request.user.ReqInitialEmailCode;
import com.bilkent.devinsight.dto.request.user.ReqSecondaryEmailCode;
import com.bilkent.devinsight.entity.enums.UserRole;
import com.bilkent.devinsight.dto.response.ApiResponse;
import com.bilkent.devinsight.dto.response.ResUserToken;
import com.bilkent.devinsight.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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

    @RequiredRole(value = {UserRole.REGISTERED_USER, UserRole.ADMIN})
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="change-email/request")
    public ResponseEntity<Object> sendChangeEmailCode(@Valid @RequestBody ReqChangeEmail reqChangeEmail) {
        userService.sendChangeEmailCode(reqChangeEmail);

        return ResponseEntity.ok(
                ApiResponse.<ResUserToken>builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Email change code sent to your old email successfully")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="change-email/verify-initial")
    public ResponseEntity<Object> verifyInitialChangeEmailCode(@Valid @RequestBody ReqInitialEmailCode
                                                                           reqInitialEmailCode) {
        userService.verifyInitialChangeEmailCode(reqInitialEmailCode);

        return ResponseEntity.ok(
                ApiResponse.<ResUserToken>builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Your code was verified. Email change code sent to your new email successfully")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="change-email/verify-secondary")
    public ResponseEntity<Object> verifySecondaryChangeEmailCode(@Valid @RequestBody ReqSecondaryEmailCode
                                                                       reqSecondaryEmailCode) {
        userService.verifySecondaryChangeEmailCode(reqSecondaryEmailCode);

        return ResponseEntity.ok(
                ApiResponse.<ResUserToken>builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Your code was verified and your email address is updated!")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }




}