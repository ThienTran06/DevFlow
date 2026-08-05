package com.hamy.devflow.auth;

import com.hamy.devflow.user.User;

import java.util.List;
import java.util.Optional;

public interface PasswordResetTokenService  {
    PasswordResetToken save(PasswordResetToken passwordResetToken);
    Optional <PasswordResetToken> findByToken (String token);
    PasswordResetToken markAsUsed(PasswordResetToken passwordResetToken);
    void delete(PasswordResetToken passwordResetToken);
    List<PasswordResetToken> findByOwner(User owner);
}
