package com.bilkent.devinsight.controller;


import com.bilkent.devinsight.response.RUser;
import com.bilkent.devinsight.request.auth.QLogin;
import com.bilkent.devinsight.request.auth.QRegister;
import com.bilkent.devinsight.request.auth.QVerifyMailAddress;
import com.bilkent.devinsight.request.user.QChangePassword;
import com.bilkent.devinsight.request.user.QResetPasswordCode;
import com.bilkent.devinsight.request.user.QResetPasswordVerifyCode;
import com.bilkent.devinsight.request.user.QResetPasswordVerifyPassword;
import com.bilkent.devinsight.response.struct.ApiResponse;
import com.bilkent.devinsight.response.ResRefreshToken;
import com.bilkent.devinsight.response.ResUserToken;
import com.bilkent.devinsight.service.AuthService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path="login")
    public ResponseEntity<Object> login(@Valid @RequestBody QLogin qLogin) {
        ResUserToken token = authService.login(qLogin);

        return ResponseEntity.ok(
                ApiResponse.<ResUserToken>builder()
                        .data(token)
                        .status(HttpStatus.OK.value())
                        .message("Login successful")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "register")
    public ResponseEntity<Object> register(@Valid @RequestBody QRegister qRegister) {
        RUser user = authService.registerUser(qRegister);

        return ResponseEntity.ok(
                ApiResponse.<RUser>builder()
                        .data(user)
                        .status(HttpStatus.OK.value())
                        .message("User registered successfully")
                        .build());
    }

    @GetMapping("refresh")
    public ResponseEntity<ApiResponse<ResRefreshToken>> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String auth) throws Exception {
        ResRefreshToken newToken = authService.refreshToken(auth);
        return ResponseEntity.ok(
                ApiResponse.<ResRefreshToken>builder()
                        .data(newToken)
                        .status(HttpStatus.OK.value())
                        .message("New access token is created")
                        .build());
    }

    @PostMapping(path = "logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Logout successful")
                        .build());
    }

    @PostMapping(path = "resend-email-verification")
    public ResponseEntity<ApiResponse<Void>> resendEmailVerification() {
        authService.resendEmailVerification();

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Verification email sent successfully")
                        .build());
    }


    @GetMapping(path = "me")
    public ResponseEntity<ApiResponse<RUser>> getCurrentUser() {
        RUser user = authService.getCurrentUserDto();

        return ResponseEntity.ok(
                ApiResponse.<RUser>builder()
                        .data(user)
                        .status(HttpStatus.OK.value())
                        .message("User fetched successfully")
                        .build());
    }


    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyMailAddress(@Valid @RequestBody QVerifyMailAddress qVerifyMailAddress) {
        authService.verifyMailAddress(qVerifyMailAddress);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Email verified successfully")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody QChangePassword qChangePassword) {
        authService.changePassword(qChangePassword);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Password changed successfully")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/reset-password/request")
    public ResponseEntity<ApiResponse<Void>> requestResetPassword(@Valid @RequestBody QResetPasswordCode qResetPasswordCode) {
        authService.requestResetPassword(qResetPasswordCode);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Reset code is sent successfully")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/reset-password/verify")
    public ResponseEntity<ApiResponse<Void>> verifyResetPasswordCode(@Valid @RequestBody QResetPasswordVerifyCode
                                                                             qResetPasswordVerifyCode) {
        authService.verifyResetPasswordCode(qResetPasswordVerifyCode);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Code verified successfully")
                        .build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, path = "/reset-password/reset")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody QResetPasswordVerifyPassword
                                                                   qResetPasswordVerifyPassword) {
        authService.resetPasswordWithCode(qResetPasswordVerifyPassword);

        return ResponseEntity.ok(
                ApiResponse.<Void>builder()
                        .status(HttpStatus.OK.value())
                        .message("Password reset successfully")
                        .build());
    }


}