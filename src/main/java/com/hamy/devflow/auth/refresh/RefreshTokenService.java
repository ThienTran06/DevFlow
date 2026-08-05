package com.hamy.devflow.auth.refresh;

import com.hamy.devflow.user.User;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken save(RefreshToken refreshToken);
    void delete(RefreshToken refreshToken);
    void revoke(RefreshToken refreshToken);
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByOwner(User owner);
    void revokeAllByOwner(User owner);
}
