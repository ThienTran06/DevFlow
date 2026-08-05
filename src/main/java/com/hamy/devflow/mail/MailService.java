package com.hamy.devflow.mail;

import com.hamy.devflow.auth.EmailVerificationToken;
import com.hamy.devflow.auth.PasswordResetToken;
import com.hamy.devflow.user.User;

public interface MailService {
    void sendVerifyMail(User user, EmailVerificationToken token);
    void sendResetPasswordMail(User user,PasswordResetToken token);
}
