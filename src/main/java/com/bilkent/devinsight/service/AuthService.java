package com.bilkent.devinsight.service;

import com.bilkent.devinsight.constants.TimeConstants;
import com.bilkent.devinsight.constants.UserConstants;
import com.bilkent.devinsight.dto.ResetPasswordEmailDto;
import com.bilkent.devinsight.dto.UserDto;
import com.bilkent.devinsight.dto.VerifyMailAddressDto;
import com.bilkent.devinsight.dto.request.auth.ReqLogin;
import com.bilkent.devinsight.dto.request.auth.ReqRegister;
import com.bilkent.devinsight.dto.request.auth.ReqVerifyMailAddress;
import com.bilkent.devinsight.dto.request.user.ReqChangePass;
import com.bilkent.devinsight.dto.request.user.ReqResetPassCode;
import com.bilkent.devinsight.dto.request.user.ReqResetPassVerifyCode;
import com.bilkent.devinsight.dto.request.user.ReqResetPassVerifyPassword;
import com.bilkent.devinsight.entity.ResetPasswordCode;
import com.bilkent.devinsight.entity.User;
import com.bilkent.devinsight.entity.VerifyMailAddressCode;
import com.bilkent.devinsight.entity.enums.UserRole;
import com.bilkent.devinsight.exception.*;
import com.bilkent.devinsight.mapper.UserMapper;
import com.bilkent.devinsight.repository.ResetPasswordRepository;
import com.bilkent.devinsight.repository.UserRepository;
import com.bilkent.devinsight.repository.VerifyMailAddressCodeRepository;
import com.bilkent.devinsight.dto.response.ResRefreshToken;
import com.bilkent.devinsight.dto.response.ResUserToken;
import com.bilkent.devinsight.security.JwtTokenUtil;
import com.bilkent.devinsight.utils.CodeUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthService {
    public static int hashStrength = 10;

    @Autowired
    final PasswordEncoder bCryptPasswordEncoder;

    private final JwtTokenUtil jwtTokenUtil;


    private final UserRepository userRepository;
    private final ResetPasswordRepository resetPasswordRepository;
    private final VerifyMailAddressCodeRepository verifyMailAddressCodeRepository;

    private final MailService mailService;


    public ResUserToken login(ReqLogin user) throws BaseException {
        Optional<User> optionalUser = userRepository.findByEmail(user.getEmail());

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User dbUser = optionalUser.get();

        String hashedPassword = dbUser.getPassword();
        boolean passwordMatch = bCryptPasswordEncoder.matches(user.getPassword(), hashedPassword);

        if (!passwordMatch) {
            throw new WrongPasswordException();
        }

        final String accessToken = jwtTokenUtil.generateAccessToken(dbUser);
        final String refreshToken = jwtTokenUtil.generateRefreshToken(dbUser);

        return ResUserToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(UserMapper.toDTO(dbUser))
                .build();
    }

    public void resendEmailVerification() {
        User user = getCurrentUserEntity();

        if (user.getEmailVerified()) {
            throw new EmailAlreadyVerifiedException();
        }

        List<VerifyMailAddressCode> verifyMailAddressCodes =
                verifyMailAddressCodeRepository.findByUserAndValidAndUsed(
                        user, true, false
                );

        for (VerifyMailAddressCode verifyMailAddressCode: verifyMailAddressCodes) {
            verifyMailAddressCode.setValid(false);
            verifyMailAddressCode.setUsed(false);
            verifyMailAddressCode.setExpireDate(new Date());
            verifyMailAddressCodeRepository.save(verifyMailAddressCode);
        }

        int code = CodeUtils.generateVerifyEmailCode();

        String hashedCode = bCryptPasswordEncoder.encode(String.valueOf(code));

        VerifyMailAddressCode verifyMailAddressCode =
                VerifyMailAddressCode.builder()
                .code(hashedCode)
                .user(user)
                .expireDate(new Date(System.currentTimeMillis() +
                        TimeConstants.SECOND_IN_MS *
                        TimeConstants.MINUTE_IN_SECONDS *
                        TimeConstants.VERIFY_MAIL_ADDRESS_TOKEN_EXPIRATION_TIME_IN_MINUTES))
                .used(false)
                .build();

        verifyMailAddressCode = verifyMailAddressCodeRepository.save(verifyMailAddressCode);

        VerifyMailAddressDto verifyMailAddressDto = VerifyMailAddressDto.builder()
                .email(user.getEmail())
                .name(user.getUsername())
                .code(code)
                .build();

        mailService.sendVerifyMailAddressEmail(verifyMailAddressDto);

        return;
    }

    public UserDto getCurrentUserDto() {
        User dbUser = getCurrentUserEntity();
        return UserMapper.toDTO(dbUser);
    }


    public ResRefreshToken refreshToken(HttpServletRequest request, String auth) {
        if (jwtTokenUtil.hasAuthorizationBearer(request)) {
            throw new TokenException("No authorization header is present", HttpStatus.UNAUTHORIZED);
        }

        String refreshToken = auth.substring(7);
        if (!jwtTokenUtil.validateRefreshToken(refreshToken)) {
            throw new TokenException("Invalid refresh token", HttpStatus.UNAUTHORIZED);
        }

        // Assuming that 'ResRefreshToken' is a DTO that contains a String 'accessToken'
//        if (isRefreshTokenExpired(refreshToken)) {
//            throw new TokenException("Expired refresh token", HttpStatus.UNAUTHORIZED);
//        }

        String username = jwtTokenUtil.extractRefreshUsername(refreshToken);
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException(HttpStatus.UNAUTHORIZED);
        }

        String newAccessToken = jwtTokenUtil.generateAccessToken(user.get());
        return ResRefreshToken.builder()
                .accessToken(newAccessToken)
                .build();
    }

    public UserDto registerUser(ReqRegister reqRegister) {
        User user = addUser(reqRegister);
        return UserMapper.toDTO(user);
    }

    public void requestResetPassword(ReqResetPassCode reqResetPassCode) {
        String email = reqResetPassCode.getEmail();
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User dbUser = optionalUser.get();

        int resetCode = CodeUtils.generateResetPasswordCode();

        ResetPasswordCode resetPasswordCode =
                ResetPasswordCode.builder()
                .code(bCryptPasswordEncoder.encode(String.valueOf(resetCode)))
                .user(dbUser)
                .expireDate(new Date(System.currentTimeMillis() +
                        TimeConstants.SECOND_IN_MS *
                        TimeConstants.MINUTE_IN_SECONDS *
                        TimeConstants.PASSWORD_RESET_TOKEN_EXPIRATION_TIME_IN_MINUTES))
                .isUsed(false)
                .build();

        resetPasswordCode = resetPasswordRepository.save(resetPasswordCode);

        ResetPasswordEmailDto resetPasswordEmailDto =
                ResetPasswordEmailDto.builder()
                .email(email)
                .name(dbUser.getName())
                .code(resetCode)
                .build();

        mailService.sendResetPasswordEmail(resetPasswordEmailDto);

        return;
    }

    public void verifyResetPasswordCode(ReqResetPassVerifyCode reqResetPassVerifyCode) {
        String email = reqResetPassVerifyCode.getEmail();
        String code = reqResetPassVerifyCode.getVerifyCode();

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User dbUser = optionalUser.get();

        Optional<ResetPasswordCode> optionalResetPasswordCode = resetPasswordRepository.findByUser(dbUser);

        if (optionalResetPasswordCode.isEmpty()) {
            throw new InvalidResetPasswordCodeException();
        }

        ResetPasswordCode resetPasswordCode = optionalResetPasswordCode.get();

        boolean codeMatch = bCryptPasswordEncoder.matches(code, resetPasswordCode.getCode());

        if (!codeMatch) {
            throw new InvalidResetPasswordCodeException();
        }

        if (resetPasswordCode.getExpireDate().before(new Date())) {
            throw new ResetPasswordCodeExpiredException();
        }

//        resetPasswordCode.setUsed(true);
//        resetPasswordRepository.save(resetPasswordCode);

        return;
    }

    public void verifyMailAddress(ReqVerifyMailAddress reqVerifyMailAddress) {
        String email = reqVerifyMailAddress.getEmail();
        String code = reqVerifyMailAddress.getCode();

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User dbUser = optionalUser.get();

        List<VerifyMailAddressCode> optionalVerifyMailAddressCode =
                verifyMailAddressCodeRepository.findByUserAndValidAndUsed(
                        dbUser, true, false
                );

        boolean isExpired = false;

        for (VerifyMailAddressCode verifyMailAddressCode: optionalVerifyMailAddressCode) {
            boolean codeMatch = bCryptPasswordEncoder.matches(code, verifyMailAddressCode.getCode());

            if (codeMatch) {
                if (verifyMailAddressCode.getExpireDate().before(new Date())) {
                    isExpired = true;
                    continue;
                }
                verifyMailAddressCode.setUsed(true);
                verifyMailAddressCode = verifyMailAddressCodeRepository.save(verifyMailAddressCode);

                dbUser.setEmailVerified(true);
                dbUser = userRepository.save(dbUser);

                return;
            }
        }

        if (isExpired) {
            throw new VerifyMailAddressCodeExpiredException();
        }

        throw new InvalidVerifyMailAddressCodeException();
    }

    public void resetPasswordWithCode(ReqResetPassVerifyPassword reqResetPassVerifyPassword) {
        String email = reqResetPassVerifyPassword.getEmail();
        String code = reqResetPassVerifyPassword.getVerifyCode();
        String newPassword = reqResetPassVerifyPassword.getNewPassword();

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        User dbUser = optionalUser.get();

        Optional<ResetPasswordCode> optionalResetPasswordCode = resetPasswordRepository.findByUser(dbUser);

        if (optionalResetPasswordCode.isEmpty()) {
            throw new InvalidResetPasswordCodeException();
        }

        ResetPasswordCode resetPasswordCode = optionalResetPasswordCode.get();
        boolean codeMatch = bCryptPasswordEncoder.matches(code, resetPasswordCode.getCode());

        if (!codeMatch) {
            throw new InvalidResetPasswordCodeException();
        }

        if (resetPasswordCode.getExpireDate().before(new Date())) {
            throw new ResetPasswordCodeExpiredException();
        }

        String hashedNewPassword = bCryptPasswordEncoder.encode(newPassword);

        dbUser.setPassword(hashedNewPassword);
        userRepository.save(dbUser);

        resetPasswordCode.setUsed(true);
        resetPasswordCode = resetPasswordRepository.save(resetPasswordCode);

        return;
    }

    protected User getCurrentUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userName = authentication.getName();

        Optional<User> optionalUserEntity = userRepository.findByEmail(userName);

        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException();
        }

        return optionalUserEntity.get();
    }

    public boolean changePassword(ReqChangePass reqChangePass) {
        User dbUser = getCurrentUserEntity();
        String hashedPassword = dbUser.getPassword();
        String oldPassword = reqChangePass.getOldPassword();

        boolean passwordMatch = bCryptPasswordEncoder.matches(oldPassword, hashedPassword);

        if (!passwordMatch) {
            throw new WrongPasswordException();
        }

        String newPassword = reqChangePass.getNewPassword();
        String hashedNewPassword = bCryptPasswordEncoder.encode(newPassword);

        dbUser.setPassword(hashedNewPassword);
        dbUser = userRepository.save(dbUser);

        return true;
    }

    public void logout() {
        User user = getCurrentUserEntity();


        // TODO: Implement revoking accessToken and refreshToken

    }

    /**
     * Adds user to the system
     * Do not return UserEntity directly, it contains password
     *
     * @param reqRegister request body
     * @return UserEntity
     */
    private User addUser(ReqRegister reqRegister) {
        String email = reqRegister.getEmail();
        String password = reqRegister.getPassword();
        String username = reqRegister.getUsername();
        String name = reqRegister.getName();

        boolean userExist = userRepository.existsByEmail(email);

        if (userExist) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        userExist = userRepository.existsByUsername(username);

        if (userExist) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }



        User user = User.builder()
                .username(username)
                .email(email)
                .name(name)
                .picture(UserConstants.DEFAULT_PROFILE_IMAGE)
                .role(UserRole.REGISTERED_USER)
                .emailVerified(false)
                .password(encodePassword(password))
                .build();

        user = userRepository.save(user);

        int code = CodeUtils.generateVerifyEmailCode();

        String hashedCode = bCryptPasswordEncoder.encode(String.valueOf(code));

        VerifyMailAddressCode verifyMailAddressCode =
                VerifyMailAddressCode.builder()
                .code(hashedCode)
                .user(user)
                .expireDate(new Date(System.currentTimeMillis() +
                        TimeConstants.SECOND_IN_MS *
                        TimeConstants.MINUTE_IN_SECONDS *
                        TimeConstants.VERIFY_MAIL_ADDRESS_TOKEN_EXPIRATION_TIME_IN_MINUTES))
                .used(false)
                .build();
        verifyMailAddressCode = verifyMailAddressCodeRepository.save(verifyMailAddressCode);


        VerifyMailAddressDto verifyMailAddressDto = VerifyMailAddressDto.builder()
                .email(email)
                .name(username)
                .code(code)
                .build();

        mailService.sendVerifyMailAddressEmail(verifyMailAddressDto);
        return user;
    }

    private String encodePassword(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }
}