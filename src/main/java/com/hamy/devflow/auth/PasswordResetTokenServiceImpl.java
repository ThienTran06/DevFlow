package com.hamy.devflow.auth;

import com.hamy.devflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
@RequiredArgsConstructor
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    @Override
    public PasswordResetToken save(PasswordResetToken passwordResetToken) {
        return passwordResetTokenRepository.save(passwordResetToken);
    }
    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }
    @Override
    public PasswordResetToken markAsUsed(PasswordResetToken passwordResetToken) {
        passwordResetToken.setUsed(true);
        return passwordResetTokenRepository.save(passwordResetToken);
    }

    @Override
    public void delete(PasswordResetToken passwordResetToken) {
        passwordResetTokenRepository.delete(passwordResetToken);
    }

    @Override
    public List<PasswordResetToken> findByOwner(User owner) {
        return passwordResetTokenRepository.findByOwner(owner);
    }

}
