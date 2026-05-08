package com.florent.application.auth;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProperties;
import com.florent.common.security.JwtProvider;
import com.florent.domain.auth.EmailAuthResult;
import com.florent.domain.auth.EmailLoginCommand;
import com.florent.domain.auth.EmailLoginUseCase;
import com.florent.domain.auth.EmailSignupCommand;
import com.florent.domain.auth.EmailSignupUseCase;
import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerRepository;
import com.florent.domain.seller.Seller;
import com.florent.domain.seller.SellerRepository;
import com.florent.domain.shop.FlowerShopRepository;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRepository;
import com.florent.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailAuthService implements EmailSignupUseCase, EmailLoginUseCase {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final FlowerShopRepository flowerShopRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    @Transactional
    public EmailAuthResult signup(EmailSignupCommand command) {
        if (userRepository.findByEmail(command.email()).isPresent()) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(command.password());
        User user = User.createFromEmail(command.email(), encodedPassword, command.nickname());
        user = userRepository.save(user);

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), null, null, null);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        LocalDateTime refreshExpiresAt = LocalDateTime.now(clock)
                .plusSeconds(jwtProperties.refreshTokenValidityMs() / 1000);
        user.updateRefreshToken(refreshToken, refreshExpiresAt);
        userRepository.save(user);

        return new EmailAuthResult(accessToken, refreshToken, null, true, false);
    }

    @Override
    @Transactional
    public EmailAuthResult login(EmailLoginCommand command) {
        User user = userRepository.findByEmail(command.email())
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (user.getPasswordHash() == null
                || !passwordEncoder.matches(command.password(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        Long buyerId = findBuyerId(user);
        Long sellerId = findSellerId(user);

        String roleName = user.getRole() != null ? user.getRole().name() : null;
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), roleName, buyerId, sellerId);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        LocalDateTime refreshExpiresAt = LocalDateTime.now(clock)
                .plusSeconds(jwtProperties.refreshTokenValidityMs() / 1000);
        user.updateRefreshToken(refreshToken, refreshExpiresAt);
        userRepository.save(user);

        boolean hasFlowerShop = false;
        if (user.getRole() == UserRole.SELLER && sellerId != null) {
            hasFlowerShop = flowerShopRepository.findBySellerId(sellerId).isPresent();
        }

        return new EmailAuthResult(accessToken, refreshToken, roleName, false, hasFlowerShop);
    }

    private Long findBuyerId(User user) {
        if (user.getRole() != UserRole.BUYER) return null;
        return buyerRepository.findByUserId(user.getId())
                .map(Buyer::getId).orElse(null);
    }

    private Long findSellerId(User user) {
        if (user.getRole() != UserRole.SELLER) return null;
        return sellerRepository.findByUserId(user.getId())
                .map(Seller::getId).orElse(null);
    }
}
