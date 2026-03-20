package com.florent.application.auth;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProperties;
import com.florent.common.security.JwtProvider;
import com.florent.domain.auth.KakaoLoginCommand;
import com.florent.domain.auth.KakaoLoginResult;
import com.florent.domain.auth.KakaoLoginUseCase;
import com.florent.domain.auth.KakaoOAuthPort;
import com.florent.domain.auth.KakaoUserInfo;
import com.florent.domain.auth.LogoutUseCase;
import com.florent.domain.auth.ReissueTokenCommand;
import com.florent.domain.auth.ReissueTokenResult;
import com.florent.domain.auth.ReissueTokenUseCase;
import com.florent.domain.auth.RegisterSellerInfoCommand;
import com.florent.domain.auth.RegisterSellerInfoResult;
import com.florent.domain.auth.RegisterSellerInfoUseCase;
import com.florent.domain.auth.SetRoleCommand;
import com.florent.domain.auth.SetRoleResult;
import com.florent.domain.auth.SetRoleUseCase;
import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerRepository;
import com.florent.domain.seller.Seller;
import com.florent.domain.seller.SellerRepository;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRepository;
import com.florent.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService implements KakaoLoginUseCase, SetRoleUseCase,
        ReissueTokenUseCase, LogoutUseCase, RegisterSellerInfoUseCase {

    private final KakaoOAuthPort kakaoOAuthPort;
    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final FlowerShopRepository flowerShopRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final Clock clock;

    @Override
    @Transactional
    public KakaoLoginResult login(KakaoLoginCommand command) {
        KakaoUserInfo kakaoUserInfo = kakaoOAuthPort.getUserInfo(command.kakaoAccessToken());

        boolean isNewUser = false;
        User user = userRepository.findByKakaoId(kakaoUserInfo.kakaoId()).orElse(null);

        if (user == null) {
            user = User.createFromKakao(
                    kakaoUserInfo.kakaoId(), kakaoUserInfo.email(), kakaoUserInfo.nickname());
            user = userRepository.save(user);
            isNewUser = true;
        } else {
            user.updateNickname(kakaoUserInfo.nickname());
            userRepository.save(user);
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

        return new KakaoLoginResult(accessToken, refreshToken, roleName, isNewUser, hasFlowerShop);
    }

    @Override
    @Transactional
    public SetRoleResult setRole(Long userId, SetRoleCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        user.assignRole(command.role());

        Long buyerId = null;
        Long sellerId = null;

        if (command.role() == UserRole.BUYER) {
            Buyer buyer = Buyer.create(userId, user.getNickname());
            buyer = buyerRepository.save(buyer);
            buyerId = buyer.getId();
        } else if (command.role() == UserRole.SELLER) {
            Seller seller = Seller.create(userId);
            seller = sellerRepository.save(seller);
            sellerId = seller.getId();
        }

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), user.getRole().name(), buyerId, sellerId);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        LocalDateTime refreshExpiresAt = LocalDateTime.now(clock)
                .plusSeconds(jwtProperties.refreshTokenValidityMs() / 1000);
        user.updateRefreshToken(refreshToken, refreshExpiresAt);
        userRepository.save(user);

        return new SetRoleResult(user.getRole().name(), accessToken, refreshToken);
    }

    @Override
    @Transactional
    public ReissueTokenResult reissue(ReissueTokenCommand command) {
        Long userId;
        try {
            var claims = jwtProvider.validateAndExtractClaims(command.refreshToken());
            userId = Long.valueOf(claims.getSubject());
        } catch (BusinessException e) {
            if (e.getErrorCode() == ErrorCode.TOKEN_EXPIRED) {
                throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
            }
            throw e;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isRefreshTokenValid(LocalDateTime.now(clock))) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        if (!command.refreshToken().equals(user.getRefreshToken())) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        Long buyerId = findBuyerId(user);
        Long sellerId = findSellerId(user);

        String roleName = user.getRole() != null ? user.getRole().name() : null;
        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), roleName, buyerId, sellerId);
        String newRefreshToken = jwtProvider.generateRefreshToken(user.getId());

        LocalDateTime refreshExpiresAt = LocalDateTime.now(clock)
                .plusSeconds(jwtProperties.refreshTokenValidityMs() / 1000);
        user.updateRefreshToken(newRefreshToken, refreshExpiresAt);
        userRepository.save(user);

        boolean hasFlowerShop = false;
        if (user.getRole() == UserRole.SELLER && sellerId != null) {
            hasFlowerShop = flowerShopRepository.findBySellerId(sellerId).isPresent();
        }

        return new ReissueTokenResult(accessToken, newRefreshToken, roleName, hasFlowerShop);
    }

    @Override
    @Transactional
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        user.clearRefreshToken();
        userRepository.save(user);
    }

    @Override
    @Transactional
    public RegisterSellerInfoResult register(Long sellerId, RegisterSellerInfoCommand command) {
        if (sellerId == null) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        if (flowerShopRepository.findBySellerId(sellerId).isPresent()) {
            throw new BusinessException(ErrorCode.SELLER_ALREADY_REGISTERED);
        }

        FlowerShop shop = FlowerShop.create(
                sellerId, command.shopName(), null, null,
                command.shopAddress(), command.shopLat(), command.shopLng());
        FlowerShop saved = flowerShopRepository.save(shop);

        return new RegisterSellerInfoResult(sellerId, saved.getShopName());
    }

    private Long findBuyerId(User user) {
        if (user.getRole() != UserRole.BUYER) {
            return null;
        }
        return buyerRepository.findByUserId(user.getId())
                .map(Buyer::getId)
                .orElse(null);
    }

    private Long findSellerId(User user) {
        if (user.getRole() != UserRole.SELLER) {
            return null;
        }
        return sellerRepository.findByUserId(user.getId())
                .map(Seller::getId)
                .orElse(null);
    }
}
