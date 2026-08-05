package com.hamy.devflow.auth;

import com.hamy.devflow.user.User;

import java.util.List;
import java.util.Optional;

public interface EmailVerificationService {
    void save(EmailVerificationToken emailVerificationToken);
    Optional<EmailVerificationToken> findByToken(String token);
    List<EmailVerificationToken> findByOwner(User owner);
    void delete(EmailVerificationToken emailVerificationToken);
}
