package com.hamy.devflow.mail;

import com.hamy.devflow.auth.EmailVerificationToken;
import com.hamy.devflow.auth.PasswordResetToken;
import com.hamy.devflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;

    @Override
    public void sendVerifyMail(User user, EmailVerificationToken token) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        String username = user.getFullName();
        UUID mailToken = UUID.fromString(token.getToken());
        simpleMailMessage.setText("DevFlow Verify Account" +
                "\nHello "+username +","+
                "\nWe send this mail to request you to verify your account."
                +"\nClick the link below to verify your account."
                +"\nhttp://localhost:8080/api/auth/reset-password?token="+mailToken
                +"\nIf you did not request this, you can safely ignore this email."
        );
        simpleMailMessage.setSubject("Verify your account");
        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendResetPasswordMail(User user, PasswordResetToken token)  {
        SimpleMailMessage simpleMailMessage =  new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        String username = user.getFullName();
        UUID mailToken = UUID.fromString(token.getToken());
        simpleMailMessage.setText("DevFlow Password Reset" +
                "\nHello "+username +","+
                "\nWe received a request to reset your password."
                +"\nClick the link below to reset your password."
                +"\nhttp://localhost:8080/api/auth/reset-password?token="+mailToken
                +"\nIf you did not request this, you can safely ignore this email."
                );
        simpleMailMessage.setSubject("Reset your password");
        javaMailSender.send(simpleMailMessage);
    }

}
