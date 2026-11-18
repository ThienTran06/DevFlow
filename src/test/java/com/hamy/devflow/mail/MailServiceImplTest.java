package com.hamy.devflow.mail;

import com.hamy.devflow.auth.EmailVerificationToken;
import com.hamy.devflow.auth.PasswordResetToken;
import com.hamy.devflow.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MailServiceImplTest {
    @Mock
    private JavaMailSender javaMailSender;

    @Test
    void sendVerifyMailUsesConfiguredFrontendUrlAndToken() {
        MailServiceImpl mailService = new MailServiceImpl(
                javaMailSender,
                "https://frontend.example/verify-email",
                "https://frontend.example/reset-password"
        );
        User user = new User();
        user.setEmail("user@example.com");
        EmailVerificationToken token = new EmailVerificationToken();
        token.setToken("verify-token");

        mailService.sendVerifyMail(user, token);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        assertTrue(messageCaptor.getValue().getText().contains(
                "https://frontend.example/verify-email?token=verify-token"
        ));
    }

    @Test
    void sendResetPasswordMailUsesConfiguredFrontendUrlAndToken() {
        MailServiceImpl mailService = new MailServiceImpl(
                javaMailSender,
                "https://frontend.example/verify-email",
                "https://frontend.example/reset-password"
        );
        User user = new User();
        user.setEmail("user@example.com");
        PasswordResetToken token = new PasswordResetToken();
        token.setToken("reset-token");

        mailService.sendResetPasswordMail(user, token);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(javaMailSender).send(messageCaptor.capture());
        assertTrue(messageCaptor.getValue().getText().contains(
                "https://frontend.example/reset-password?token=reset-token"
        ));
    }
}
