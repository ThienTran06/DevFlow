package com.hamy.devflow.auth;

import com.hamy.devflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class EmailVerificationServiceImpl implements EmailVerificationService {
    private final EmailVerificationRepository emailVerificationRepository;
    @Override
    public void save(EmailVerificationToken emailVerificationToken) {
        emailVerificationRepository.save(emailVerificationToken);
    }

    @Override
    public Optional<EmailVerificationToken> findByToken(String token) {
        return emailVerificationRepository.findByToken(token);
    }

    @Override
    public List<EmailVerificationToken> findByOwner(User owner) {
        return emailVerificationRepository.findByOwner(owner);
    }

    @Override
    public void delete(EmailVerificationToken emailVerificationToken) {
        emailVerificationRepository.delete(emailVerificationToken);
    }
}
