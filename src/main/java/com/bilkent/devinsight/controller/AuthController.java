package com.bilkent.devinsight.controller;


import com.bilkent.devinsight.dto.UserDto;
import com.bilkent.devinsight.dto.request.auth.ReqLogin;
import com.bilkent.devinsight.dto.request.auth.ReqRegister;
import com.bilkent.devinsight.dto.request.auth.ReqVerifyMailAddress;
import com.bilkent.devinsight.dto.request.user.ReqChangePass;
import com.bilkent.devinsight.dto.request.user.ReqResetPassCode;
import com.bilkent.devinsight.dto.request.user.ReqResetPassVerifyCode;
import com.bilkent.devinsight.dto.request.user.ReqResetPassVerifyPassword;
import com.bilkent.devinsight.dto.response.ApiResponse;
import com.bilkent.devinsight.dto.response.ResUserToken;
import com.bilkent.devinsight.service.AuthService;
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
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="login")
    public ResponseEntity<Object> login(@Valid @RequestBody ReqLogin reqLogin) {
        ResUserToken token = authService.login(reqLogin);

        return ResponseEntity.ok(
                ApiResponse.<ResUserToken>builder()
                        .operationResultData(token)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Login successful")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "register")
    public ResponseEntity<Object> register(@Valid @RequestBody ReqRegister reqRegister) {
        UserDto user = authService.registerUser(reqRegister);

        return ResponseEntity.ok(
                ApiResponse.<UserDto>builder()
                        .operationResultData(user)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("User registered successfully")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "logout")
    public ResponseEntity<Object> logout() {
        authService.logout();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Logout successful")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "resend-email-verification")
    public ResponseEntity<Object> resendEmailVerification() {
        authService.resendEmailVerification();

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Verification email resent")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "me")
    public ResponseEntity<Object> register() {
        UserDto user = authService.getCurrentUserDto();

        return ResponseEntity.ok(
                ApiResponse.<UserDto>builder()
                        .operationResultData(user)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Successfully fetched user info")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "verify-email")
    public ResponseEntity<Object> verifyMailAddress(@Valid @RequestBody ReqVerifyMailAddress reqVerifyMailAddress) {
        authService.verifyMailAddress(reqVerifyMailAddress);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Email verified successfully")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "change-password")
    public ResponseEntity<Object> changePassword(@Valid @RequestBody ReqChangePass reqChangePass) {
        authService.changePassword(reqChangePass);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Password changed successfully")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/reset-password/request")
    public ResponseEntity<Object> requestResetPassword(@Valid @RequestBody ReqResetPassCode reqResetPassCode) {
        authService.requestResetPassword(reqResetPassCode);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Reset code is sent to your email")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/reset-password/verify")
    public ResponseEntity<Object> verifyResetPasswordCode(@Valid @RequestBody ReqResetPassVerifyCode
                                                                      reqResetPassVerifyCode) {
        authService.verifyResetPasswordCode(reqResetPassVerifyCode);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Reset code is verified")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/reset-password/reset")
    public ResponseEntity<Object> resetPassword(@Valid @RequestBody ReqResetPassVerifyPassword
                                                            reqResetPassVerifyPassword) {
        authService.resetPasswordWithCode(reqResetPassVerifyPassword);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .operationResultData(null)
                        .operationResult(ApiResponse.OperationResult.builder()
                                .returnMessage("Password is reset")
                                .returnCode("0")
                                .returnErrors(null)
                                .build())
                        .build());
    }


}