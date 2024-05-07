package com.bilkent.devinsight.service;

import com.bilkent.devinsight.constants.TimeConstants;
import com.bilkent.devinsight.constants.UserConstants;
import com.bilkent.devinsight.response.email.REmailResetPassword;
import com.bilkent.devinsight.response.RUser;
import com.bilkent.devinsight.response.email.REmailVerifyMailAddress;
import com.bilkent.devinsight.request.auth.QLogin;
import com.bilkent.devinsight.request.auth.QRegister;
import com.bilkent.devinsight.request.auth.QVerifyMailAddress;
import com.bilkent.devinsight.request.user.QChangePassword;
import com.bilkent.devinsight.request.user.QResetPasswordCode;
import com.bilkent.devinsight.request.user.QResetPasswordVerifyCode;
import com.bilkent.devinsight.request.user.QResetPasswordVerifyPassword;
import com.bilkent.devinsight.entity.ResetPasswordCode;
import com.bilkent.devinsight.entity.User;
import com.bilkent.devinsight.entity.VerifyMailAddressCode;
import com.bilkent.devinsight.entity.enums.UserRole;
import com.bilkent.devinsight.exception.*;
import com.bilkent.devinsight.mapper.UserMapper;
import com.bilkent.devinsight.repository.ResetPasswordRepository;
import com.bilkent.devinsight.repository.UserRepository;
import com.bilkent.devinsight.repository.VerifyMailAddressCodeRepository;
import com.bilkent.devinsight.response.ResRefreshToken;
import com.bilkent.devinsight.response.ResUserToken;
import com.bilkent.devinsight.security.JWTUserService;
import com.bilkent.devinsight.security.JWTUtils;
import com.bilkent.devinsight.utils.CodeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class AuthService {
    public static int hashStrength = 10;

    @Autowired
    final PasswordEncoder bCryptPasswordEncoder;

    private final JWTUtils jwtUtils;
    private final JWTUserService jwtUserService;

    private final UserRepository userRepository;
    private final ResetPasswordRepository resetPasswordRepository;
    private final VerifyMailAddressCodeRepository verifyMailAddressCodeRepository;

    private final MailService mailService;


    public ResUserToken login(QLogin user) throws BaseException {
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

        final UserDetails userDetails = jwtUserService.loadUserByUsername(user.getEmail());
        final String accessToken = jwtUtils.createAccessToken(userDetails);
        final String refreshToken = jwtUtils.createRefreshToken(userDetails);

        log.info("User logged in: {}", user.getEmail());

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

        REmailVerifyMailAddress rEmailVerifyMailAddress = REmailVerifyMailAddress.builder()
                .email(user.getEmail())
                .name(user.getUsername())
                .code(code)
                .build();

        mailService.sendVerifyMailAddressEmail(rEmailVerifyMailAddress);

        return;
    }

    public RUser getCurrentUserDto() {
        User dbUser = getCurrentUserEntity();
        return UserMapper.toDTO(dbUser);
    }


    public ResRefreshToken refreshToken(String auth) {

        String username = null;
        try {
            username = jwtUtils.extractRefreshUsername(jwtUtils.getTokenWithoutBearer(auth));
        } catch (Exception e) {
            log.error("Error in generating new token with refresh token", e);
            throw new SomethingWentWrongException();
        }

        final UserDetails userDetails = jwtUserService.loadUserByUsername(username);
        final String accessToken = jwtUtils.createAccessToken(userDetails);

        return ResRefreshToken.builder()
                .accessToken(accessToken)
                .build();
    }

    public RUser registerUser(QRegister qRegister) {
        User user = addUser(qRegister);
        return UserMapper.toDTO(user);
    }

    public void requestResetPassword(QResetPasswordCode qResetPasswordCode) {
        String email = qResetPasswordCode.getEmail();
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

        REmailResetPassword rEmailResetPassword =
                REmailResetPassword.builder()
                .email(email)
                .name(dbUser.getName())
                .code(resetCode)
                .build();

        mailService.sendResetPasswordEmail(rEmailResetPassword);

        return;
    }

    public void verifyResetPasswordCode(QResetPasswordVerifyCode qResetPasswordVerifyCode) {
        String email = qResetPasswordVerifyCode.getEmail();
        String code = qResetPasswordVerifyCode.getVerifyCode();

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

    public void verifyMailAddress(QVerifyMailAddress qVerifyMailAddress) {
        String email = qVerifyMailAddress.getEmail();
        String code = qVerifyMailAddress.getCode();

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

    public void resetPasswordWithCode(QResetPasswordVerifyPassword qResetPasswordVerifyPassword) {
        String email = qResetPasswordVerifyPassword.getEmail();
        String code = qResetPasswordVerifyPassword.getVerifyCode();
        String newPassword = qResetPasswordVerifyPassword.getNewPassword();

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
        System.out.println(userName);

        Optional<User> optionalUserEntity = userRepository.findByEmail(userName);

        if (optionalUserEntity.isEmpty()) {
            throw new UserNotFoundException();
        }

        return optionalUserEntity.get();
    }

    public boolean changePassword(QChangePassword qChangePassword) {
        User dbUser = getCurrentUserEntity();
        String hashedPassword = dbUser.getPassword();
        String oldPassword = qChangePassword.getOldPassword();

        boolean passwordMatch = bCryptPasswordEncoder.matches(oldPassword, hashedPassword);

        if (!passwordMatch) {
            throw new WrongPasswordException();
        }

        String newPassword = qChangePassword.getNewPassword();
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
     * @param qRegister request body
     * @return UserEntity
     */
    private User addUser(QRegister qRegister) {
        String email = qRegister.getEmail();
        String password = qRegister.getPassword();
        String username = qRegister.getUsername();
        String name = qRegister.getName();

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


        REmailVerifyMailAddress rEmailVerifyMailAddress = REmailVerifyMailAddress.builder()
                .email(email)
                .name(username)
                .code(code)
                .build();

        mailService.sendVerifyMailAddressEmail(rEmailVerifyMailAddress);
        return user;
    }

    private String encodePassword(String plainPassword) {
        return bCryptPasswordEncoder.encode(plainPassword);
    }
}