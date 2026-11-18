package com.hamy.devflow.mail;

import com.hamy.devflow.auth.EmailVerificationToken;
import com.hamy.devflow.auth.PasswordResetToken;
import com.hamy.devflow.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;
    private final String verifyEmailUrl;
    private final String resetPasswordUrl;

    public MailServiceImpl(
            JavaMailSender javaMailSender,
            @Value("${app.frontend.verify-email-url}") String verifyEmailUrl,
            @Value("${app.frontend.reset-password-url}") String resetPasswordUrl) {
        this.javaMailSender = javaMailSender;
        this.verifyEmailUrl = verifyEmailUrl;
        this.resetPasswordUrl = resetPasswordUrl;
    }

    @Override
    public void sendVerifyMail(User user, EmailVerificationToken token) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        String username = user.getFullName();
        simpleMailMessage.setText("DevFlow Verify Account" +
                "\nHello "+username +","+
                "\nWe send this mail to request you to verify your account."
                +"\nClick the link below to verify your account."
                +"\n"+buildTokenUrl(verifyEmailUrl, token.getToken())
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
        simpleMailMessage.setText("DevFlow Password Reset" +
                "\nHello "+username +","+
                "\nWe received a request to reset your password."
                +"\nClick the link below to reset your password."
                +"\n"+buildTokenUrl(resetPasswordUrl, token.getToken())
                +"\nIf you did not request this, you can safely ignore this email."
                );
        simpleMailMessage.setSubject("Reset your password");
        javaMailSender.send(simpleMailMessage);
    }

    private String buildTokenUrl(String frontendUrl, String token) {
        return UriComponentsBuilder.fromUriString(frontendUrl)
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
    }
}
