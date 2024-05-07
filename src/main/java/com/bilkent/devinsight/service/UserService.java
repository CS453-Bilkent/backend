package com.bilkent.devinsight.service;

import com.bilkent.devinsight.constants.TimeConstants;
import com.bilkent.devinsight.response.email.REmailChangeMail;
import com.bilkent.devinsight.response.RUser;
import com.bilkent.devinsight.request.user.QChangeEmail;
import com.bilkent.devinsight.request.user.QInitialEmailCode;
import com.bilkent.devinsight.request.user.QSecondaryEmailCode;
import com.bilkent.devinsight.entity.ChangeEmailCode;
import com.bilkent.devinsight.entity.User;
import com.bilkent.devinsight.entity.enums.ChangeEmailCodeType;
import com.bilkent.devinsight.exception.ChangeEmailCodeExpiredException;
import com.bilkent.devinsight.exception.InvalidChangeEmailCodeException;
import com.bilkent.devinsight.exception.UserNotFoundException;
import com.bilkent.devinsight.mapper.UserMapper;
import com.bilkent.devinsight.repository.ChangeEmailCodeRepository;
import com.bilkent.devinsight.repository.UserRepository;
import com.bilkent.devinsight.utils.CodeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final ChangeEmailCodeRepository changeEmailCodeRepository;

    private final MailService mailService;
    private final AuthService authService;

    private final PasswordEncoder passwordEncoder;

    public RUser createUser(RUser rUser) {
        User user = new User();
        user.setName(rUser.getName());
        user.setEmail(rUser.getEmail());
        // set other fields as necessary
        user = userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    public void sendChangeEmailCode(QChangeEmail qChangeEmail) {
        String newMailAddress = qChangeEmail.getNewEmail();
        User user = authService.getCurrentUserEntity();

        int code = CodeUtils.generateChangeEmailCode();

        List<ChangeEmailCode> optionalChangeEmailCode =
                changeEmailCodeRepository.findByUserAndValidAndUsed(user, true, false);

        for (ChangeEmailCode changeEmailCode : optionalChangeEmailCode) {
            changeEmailCode.setValid(false);
            changeEmailCode.setUsedDate(new Date());
            changeEmailCodeRepository.save(changeEmailCode);
        }

        ChangeEmailCode changeEmailCode = ChangeEmailCode.builder()
                .user(user)
                .code(passwordEncoder.encode(String.valueOf(code)))
                .expireDate(new Date(System.currentTimeMillis() +
                        TimeConstants.SECOND_IN_MS *
                        TimeConstants.MINUTE_IN_SECONDS * TimeConstants.CHANGE_EMAIL_TOKEN_EXPIRATION_TIME_IN_MINUTES))
                .newEmail(newMailAddress)
                .changeEmailCodeType(ChangeEmailCodeType.INITIAL)
                .used(false)
                .build();
        changeEmailCodeRepository.save(changeEmailCode);

        REmailChangeMail changeMailAddressEmailDto = REmailChangeMail.builder()
                .name(user.getName())
                .email(user.getEmail())
                .newEmail(newMailAddress)
                .code(code)
                .build();

        mailService.sendChangeMailAddressEmail(changeMailAddressEmailDto);
        return;
    }

    public void verifyInitialChangeEmailCode(QInitialEmailCode qInitialEmailCode) {
        int userCode = qInitialEmailCode.getCode();
        User user = authService.getCurrentUserEntity();

        Optional<ChangeEmailCode> optionalChangeEmailCode =
                changeEmailCodeRepository.findByUserAndValidAndUsedAndChangeEmailCodeType(user, true,
                        false, ChangeEmailCodeType.INITIAL);

        if (optionalChangeEmailCode.isEmpty()) {
            throw new InvalidChangeEmailCodeException();
        }

        ChangeEmailCode changeEmailCode = optionalChangeEmailCode.get();

        if (changeEmailCode.getExpireDate().before(new Date())) {
            changeEmailCode.setValid(false);
            changeEmailCode.setUsedDate(new Date());
            changeEmailCodeRepository.save(changeEmailCode);
            throw new ChangeEmailCodeExpiredException();
        }

        if (!passwordEncoder.matches(String.valueOf(userCode), changeEmailCode.getCode())) {
            throw new InvalidChangeEmailCodeException();
        }

        int secondaryCode = CodeUtils.generateChangeEmailCode();

        ChangeEmailCode secondaryEmailCode = ChangeEmailCode.builder()
                .user(user)
                .code(passwordEncoder.encode(String.valueOf(secondaryCode)))
                .expireDate(new Date(System.currentTimeMillis() +
                        TimeConstants.SECOND_IN_MS *
                        TimeConstants.MINUTE_IN_SECONDS * TimeConstants.CHANGE_EMAIL_TOKEN_EXPIRATION_TIME_IN_MINUTES))
                .newEmail(changeEmailCode.getNewEmail())
                .used(false)
                .changeEmailCodeType(ChangeEmailCodeType.SECONDARY)
                .build();
        changeEmailCodeRepository.save(secondaryEmailCode);

        REmailChangeMail changeMailAddressEmailDto = REmailChangeMail.builder()
                .name(user.getName())
                .email(user.getEmail())
                .newEmail(user.getEmail())
                .code(secondaryCode)
                .build();

        mailService.sendChangeMailAddressEmail(changeMailAddressEmailDto);
        return;
    }

    public void verifySecondaryChangeEmailCode(QSecondaryEmailCode qSecondaryEmailCode) {
        int userCode = qSecondaryEmailCode.getCode();
        User user = authService.getCurrentUserEntity();

        Optional<ChangeEmailCode> optionalChangeEmailCode =
                changeEmailCodeRepository.findByUserAndValidAndUsedAndChangeEmailCodeType(user, true,
                        false, ChangeEmailCodeType.SECONDARY);

        if (optionalChangeEmailCode.isEmpty()) {
            throw new InvalidChangeEmailCodeException();
        }

        ChangeEmailCode changeEmailCode = optionalChangeEmailCode.get();

        if (changeEmailCode.getExpireDate().before(new Date())) {
            changeEmailCode.setValid(false);
            changeEmailCode.setUsedDate(new Date());
            changeEmailCodeRepository.save(changeEmailCode);
            throw new ChangeEmailCodeExpiredException();
        }

        if (!passwordEncoder.matches(String.valueOf(userCode), changeEmailCode.getCode())) {
            throw new InvalidChangeEmailCodeException();
        }

        changeEmailCode.setUsed(true);
        changeEmailCode.setUsedDate(new Date());
        changeEmailCodeRepository.save(changeEmailCode);

        user.setEmail(changeEmailCode.getNewEmail());
        userRepository.save(user);

        //TODO: Maybe invalidate all logins here.
        return;
    }

    public RUser updateUser(UUID id, RUser rUser) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setName(rUser.getName());
        user.setEmail(rUser.getEmail());

        // update other fields as necessary
        user = userRepository.save(user);
        return UserMapper.toDTO(user);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public RUser getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return UserMapper.toDTO(user);
    }

}

