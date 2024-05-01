package com.bilkent.devinsight.service;

import com.bilkent.devinsight.constants.MailConstants;
import com.bilkent.devinsight.response.email.REmailChangeMail;
import com.bilkent.devinsight.response.email.REmailResetPassword;
import com.bilkent.devinsight.response.email.REmailVerifyMailAddress;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String defaultSender;

    @Value("${frontend.url}")
    private String frontendUrl;

    @Async
    public void sendVerifyMailAddressEmail(REmailVerifyMailAddress rEmailVerifyMailAddress) {
        String subject = "DevInsight Email Verification";
        String message = "Hi " + rEmailVerifyMailAddress.getName() + "!\n" +
                "Your account has been registered.\n" +
                "Please use the code below to verify your email: \n" +
                "This code is valid for 1 hour. After that, it will be expired.\n" +
                "This code cannot be used more than once.\n" +
                "Your code is: " + rEmailVerifyMailAddress.getCode() + "\n\n" +
                "If you did not request password reset, kindly ignore this email.";

        try {
            javaMailSender.send(generateMailMessage(rEmailVerifyMailAddress.getEmail(), defaultSender, subject, message));
        } catch (Exception exception) {
            log.error("Error sending verify email address mail to " + rEmailVerifyMailAddress.getEmail() + ", " + exception.getMessage());
        }
    }

//    @Async
//    public void sendForgotPasswordEmail(ForgotPasswordEmailDto forgotPasswordEmailDto) {
//        String subject = forgotPasswordEmailDto.getCode() + " is your DevInsight Forgot Password Code";
//        String message = "Hi " + forgotPasswordEmailDto.getName() + "!\n" +
//                "You are receiving this email because you requested to change your password.\n" +
//                "This code is valid for 1 hour. After that, it will be expired.\n" +
//                "This code cannot be used more than once.\n" +
//                "Your code is: " + forgotPasswordEmailDto.getCode() + "\n\n" +
//                "If you did not request password change, kindly ignore this email.";
//        try {
//            javaMailSender.send(generateMailMessage(forgotPasswordEmailDto.getEmail(), defaultSender, subject, message));
//        } catch (Exception exception) {
//            log.error("Error sending forgot password email to " + forgotPasswordEmailDto.getEmail() + ", " + exception.getMessage());
//        }
//    }


    @Async
    public void sendChangeMailAddressEmail(REmailChangeMail changeMailAddressEmailDto) {
        String subject = changeMailAddressEmailDto.getCode() + " is your DevInsight Change Email Code";
        String message = "Hi " + changeMailAddressEmailDto.getName() + "!\n" +
                "You are receiving this email because you requested to change your email to " +
                changeMailAddressEmailDto.getNewEmail() + ".\n" +
                "This code is valid for 1 hour. After that, it will be expired.\n" +
                "This code cannot be used more than once.\n" +
                "Your code is: " + changeMailAddressEmailDto.getCode() + "\n\n" +
                "If you did not request password reset, kindly ignore this email.";
        try {
            javaMailSender.send(generateMailMessage(changeMailAddressEmailDto.getEmail(), defaultSender, subject, message));
        } catch (Exception exception) {
            log.error("Error sending forgot password email to " + changeMailAddressEmailDto.getEmail() + ", " + exception.getMessage());
        }
    }


    @Async
    public void sendVerifyChangeMailAddressEmail(REmailChangeMail changeMailAddressEmailDto) {
        String subject = changeMailAddressEmailDto.getCode() + " is your DevInsight Change Email Code";
        String message = "Hi " + changeMailAddressEmailDto.getName() + "!\n" +
                "You are receiving this email because you requested to change your email from " +
                changeMailAddressEmailDto.getEmail() + ".\n" +
                "This code is valid for 1 hour. After that, it will be expired.\n" +
                "This code cannot be used more than once.\n" +
                "Your code is: " + changeMailAddressEmailDto.getCode() + "\n\n" +
                "If you did not request password reset, kindly ignore this email.";
        try {
            javaMailSender.send(generateMailMessage(changeMailAddressEmailDto.getNewEmail(), defaultSender, subject, message));
        } catch (Exception exception) {
            log.error("Error sending forgot password email to " + changeMailAddressEmailDto.getNewEmail() + ", " + exception.getMessage());
        }
    }



    @Async
    public void sendResetPasswordEmail(REmailResetPassword rEmailResetPassword) {
        String subject = rEmailResetPassword.getCode() + " is your DevInsight Password Reset Code";
        String message = "Hi " + rEmailResetPassword.getName() + "!\n" +
                "You are receiving this email because you requested to reset your password.\n" +
                "This code is valid for 1 hour. After that, it will be expired.\n" +
                "This code cannot be used more than once.\n" +
                "Your code is: " + rEmailResetPassword.getCode() + "\n\n" +
                "If you did not request password reset, kindly ignore this email.";
        try {
            javaMailSender.send(generateMailMessage(rEmailResetPassword.getEmail(), defaultSender, subject, message));
        } catch (Exception exception) {
            log.error("Error sending forgot password email to " + rEmailResetPassword.getEmail() + ", " + exception.getMessage());
        }
    }

    private SimpleMailMessage generateMailMessage(String to, String from, String subject, String message) {
        SimpleMailMessage mail = new SimpleMailMessage();

        mail.setTo(to);
        mail.setFrom(from);
        mail.setSubject(subject);
        mail.setText(message);

        return mail;
    }

    private void sendBulkMailMessages(SimpleMailMessage[] simpleMailMessages) {
        int totalMessageCount = simpleMailMessages.length;
        int sentCount = 0;

        log("Started sending " + totalMessageCount + " messages!");

        while (sentCount < totalMessageCount) {
            int count = 0;
            int currentBatchSize = Math.min(totalMessageCount - sentCount, MailConstants.BATCH_SIZE);
            SimpleMailMessage[] currentBatchMailMessages = new SimpleMailMessage[currentBatchSize];

            while (count < currentBatchSize) {
                currentBatchMailMessages[count] = simpleMailMessages[count + sentCount];
                count += 1;
            }

            sentCount += currentBatchSize;

            try {
                javaMailSender.send(currentBatchMailMessages);

                log("Mails sent to: " +
                        Arrays.stream(currentBatchMailMessages)
                                .map(SimpleMailMessage::getTo)
                                .map(Arrays::toString)
                                .collect(Collectors.joining (", ")));
            } catch (Exception exception) {
                log("Error sending emails to: " +
                        Arrays.stream(currentBatchMailMessages)
                                .map(SimpleMailMessage::getTo)
                                .map(Arrays::toString)
                                .collect(Collectors.joining (", ")) + " -- Error: " + exception.getMessage());
            }

            try {
                log("Sleeping for " + MailConstants.BATCH_SLEEP_IN_MS + "ms ");
                Thread.sleep(MailConstants.BATCH_SLEEP_IN_MS);
                log("Slept for " + MailConstants.BATCH_SLEEP_IN_MS + "ms");
            } catch (InterruptedException e) {
                log("Mail thread could not sleep!");
            }
        }

        log("All mails are sent!");
    }

    private String getDateNow() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());

        return formatter.format(date);
    }

    private void log(String message) {
        System.out.println("[MAIL] " + getDateNow() + " - " + message);
    }
}
