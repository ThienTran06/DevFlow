package com.hamy.devflow.auth.refresh;

import com.hamy.devflow.user.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RefreshTokenServiceImpl  implements RefreshTokenService {
    RefreshTokenRepository refreshTokenRepository;
    RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    @Override
    public void revoke(RefreshToken refreshToken) {
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
       return refreshTokenRepository.findByToken(token);
    }

    @Override
    public List<RefreshToken> findByOwner(User owner) {
        return refreshTokenRepository.findByOwner(owner);
    }
    @Transactional
    @Override
    public void revokeAllByOwner(User owner) {
       List<RefreshToken> tokens = refreshTokenRepository.findByOwner(owner);
       tokens.forEach(refreshToken -> {
           refreshToken.setRevoked(true);
       });
        refreshTokenRepository.saveAll(tokens);
    }
}
