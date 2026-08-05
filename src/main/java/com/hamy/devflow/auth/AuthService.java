package com.hamy.devflow.auth;

import com.hamy.devflow.auth.dto.*;
import com.hamy.devflow.auth.refresh.RefreshToken;
import com.hamy.devflow.auth.refresh.RefreshTokenService;
import com.hamy.devflow.common.exception.BadRequestException;
import com.hamy.devflow.common.exception.ResourceNotFoundException;
import com.hamy.devflow.common.exception.UnauthorizedException;
import com.hamy.devflow.mail.MailService;
import com.hamy.devflow.user.*;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final MailService mailService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetTokenService passwordResetTokenService;

    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
            LocalDateTime now = LocalDateTime.now();
            Optional <User> user =userRepository.findByEmail(registerRequest.getEmail());
            if (user.isPresent()) {
                if(!user.get().getStatus().equals(UserStatus.PENDING)){
                    throw new BadRequestException("Email already exists");
                }
                else{
                    List<EmailVerificationToken> oldToken = emailVerificationService.findByOwner(user.get());
                    for(EmailVerificationToken token : oldToken){
                        emailVerificationService.delete(token);
                    }
                   createAndSendVerifyMail(user.get(),now);
                   return new AuthResponse(user.get().getId(), user.get().getFullName(), user.get().getEmail(),user.get().getRole(),null,"Resend verify mail",null);
                }
            }
            User newUser = new User();
            String encodedPassword  = passwordEncoder.encode(registerRequest.getPassword());
            newUser.setPassword(encodedPassword);
            newUser.setRole(UserRole.USER);
            newUser.setEmail(registerRequest.getEmail());
            newUser.setCreatedAt(now);
            newUser.setUpdatedAt(now);
            newUser.setFullName(registerRequest.getFullName());
            newUser.setStatus(UserStatus.PENDING);
            User savedUser = userRepository.save(newUser);
           createAndSendVerifyMail(savedUser,now);
            return new AuthResponse(savedUser.getId(), savedUser.getFullName(), savedUser.getEmail(),savedUser.getRole(),null,"Registered Successfully",null);
    }
    private void createAndSendVerifyMail(User user, LocalDateTime now) {
        String token = UUID.randomUUID().toString();
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken();
        emailVerificationToken.setToken(token);
        emailVerificationToken.setCreatedAt(now);
        emailVerificationToken.setExpiryDate(now.plusDays(30));
        emailVerificationToken.setOwner(user);
        emailVerificationToken.setVerified(false);
        emailVerificationService.save(emailVerificationToken);
        mailService.sendVerifyMail(user,emailVerificationToken);
    }
    public AuthResponse login(LoginRequest loginRequest) {
        Optional <User> user =userRepository.findByEmail(loginRequest.getEmail());
        if (user.isEmpty()) {
            throw new BadRequestException("Email does not exists");
        }
        User existingUser = user.get();
        if(!passwordEncoder.matches(loginRequest.getPassword(),existingUser.getPassword())) {
            throw new BadRequestException("Wrong Password");
        }
        String accessToken = jwtService.generateAccessToken(existingUser);
        String refreshToken = jwtService.generateRefreshToken(existingUser);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        Claims claims = jwtService.parseRefreshClaims(refreshToken);
        refreshTokenEntity.setCreatedAt(jwtService.extractIssuedAt(claims).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime());
        refreshTokenEntity.setExpiredAt(jwtService.extractExpiration(claims).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        refreshTokenEntity.setOwner(existingUser);
        refreshTokenEntity.setRevoked(false);
        refreshTokenService.save(refreshTokenEntity);
        return new AuthResponse(existingUser.getId(), existingUser.getFullName(), existingUser.getEmail(),existingUser.getRole(),accessToken,"Logged In",refreshToken);
    }
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.getRefreshToken();
        Claims claims = jwtService.parseRefreshClaims(refreshToken);
        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken).orElseThrow(()->new UnauthorizedException("Refresh Token not found"));
        if(refreshTokenEntity.getRevoked()) {
            throw new UnauthorizedException("Refresh Token has been revoked");
        }
        User owner =  refreshTokenEntity.getOwner();
        Long userId = Long.parseLong(jwtService.extractUserId(claims));
        if(owner == null) {
            throw new UnauthorizedException("User request refresh token does not exist");
        }
       if(!owner.getId().equals(userId)) {
           throw new UnauthorizedException("Invalid Refresh Token");
       }
       String newAccessToken = jwtService.generateAccessToken(owner);
       String newRefreshToken = jwtService.generateRefreshToken(owner);
       RefreshToken newRefreshTokenEntity = new RefreshToken();
       newRefreshTokenEntity.setToken(newRefreshToken);
       claims = jwtService.parseRefreshClaims(newRefreshToken);
       newRefreshTokenEntity.setCreatedAt(jwtService.extractIssuedAt(claims).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
       newRefreshTokenEntity.setExpiredAt(jwtService.extractExpiration(claims).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
       newRefreshTokenEntity.setOwner(owner);
       newRefreshTokenEntity.setRevoked(false);
       refreshTokenService.save(newRefreshTokenEntity);
       refreshTokenService.revoke(refreshTokenEntity);
       return new AuthResponse(owner.getId(), owner.getFullName(), owner.getEmail(),owner.getRole(),newAccessToken,"Refresh successfully",newRefreshToken);
    }
    @Transactional
    public void logout(LogoutRequest logoutRequest) {
        String refreshToken = logoutRequest.getRefreshToken();
        Claims claims = jwtService.parseRefreshClaims(refreshToken);
        RefreshToken refreshTokenEntity = refreshTokenService.findByToken(refreshToken).orElseThrow(()->new UnauthorizedException("Refresh Token not found"));
        if(refreshTokenEntity.getRevoked()) {
            throw new UnauthorizedException("Refresh Token has been revoked");
        }
        User owner =  refreshTokenEntity.getOwner();
        Long userId = Long.parseLong(jwtService.extractUserId(claims));
        if(owner == null) {
            throw new UnauthorizedException("User request refresh token does not exist");
        }
        if(!owner.getId().equals(userId)) {
            throw new UnauthorizedException("Invalid Refresh Token");
        }
        refreshTokenService.revoke(refreshTokenEntity);
    }
    @Transactional
    public void forgotPassword(ForgotPasswordRequest forgotPasswordRequest) {
        String email  = forgotPasswordRequest.getEmail();
        UUID uuid = UUID.randomUUID();
        User user = userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("Email not found"));
        List<PasswordResetToken> oldToken = passwordResetTokenService.findByOwner(user);
        for(PasswordResetToken token : oldToken) {
            passwordResetTokenService.markAsUsed(token);
        }
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(uuid.toString());
        passwordResetToken.setOwner(user);
        passwordResetToken.setCreatedAt(LocalDateTime.now());
        passwordResetToken.setExpiredAt(LocalDateTime.now().plusHours(1));
        passwordResetToken.setUsed(false);
        passwordResetTokenService.save(passwordResetToken);
        mailService.sendResetPasswordMail(user, passwordResetToken);
    }
    @Transactional
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String token = resetPasswordRequest.getToken();
        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token).orElseThrow(()->new UnauthorizedException("Token not found"));
        if(passwordResetToken.isUsed())throw new UnauthorizedException("Token has been used");
        if(passwordResetToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Token has expired");
        }
        User owner = passwordResetToken.getOwner();
        if (!userRepository.existsById(owner.getId())){
            throw new UnauthorizedException("User request reset token does not exist");
        }
        String newPassword = resetPasswordRequest.getNewPassword();
        String encodedNewPassword = passwordEncoder.encode(newPassword);
        owner.setPassword(encodedNewPassword);
        owner.setUpdatedAt(LocalDateTime.now());
        userRepository.save(owner);
        passwordResetTokenService.markAsUsed(passwordResetToken);
        refreshTokenService.revokeAllByOwner(owner);
    }
    @Transactional
    public void verifyAccount(VerifyAccountRequest request){
        String token = request.getToken();
        EmailVerificationToken emailVerificationToken = emailVerificationService.findByToken(token).orElseThrow(()->new UnauthorizedException("Token not found"));
        if(emailVerificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Token has expired");
        }
        User owner = emailVerificationToken.getOwner();
        if(!userRepository.existsById(owner.getId())) {
            throw new UnauthorizedException("User request verification token does not exist");
        }
        owner.setStatus(UserStatus.ACTIVE);
        owner.setUpdatedAt(LocalDateTime.now());
        userRepository.save(owner);
        emailVerificationService.delete(emailVerificationToken);
    }
}
