package com.hamy.devflow.auth;

import com.hamy.devflow.auth.refresh.RefreshTokenRepository;
import com.hamy.devflow.auth.refresh.RefreshTokenService;
import com.hamy.devflow.user.User;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

@Service
@RequiredArgsConstructor
public class RevokeTokenService {
    private final RefreshTokenService refreshTokenService;
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeAllByOwner(User owner) {
        refreshTokenService.revokeAllByOwner(owner);
    }
}
