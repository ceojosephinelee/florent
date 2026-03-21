package com.florent.application.auth;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProperties;
import com.florent.common.security.JwtProvider;
import com.florent.domain.auth.DevLoginUseCase;
import com.florent.domain.auth.KakaoLoginResult;
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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
@Profile({"local", "prod"})
@RequiredArgsConstructor
public class DevAuthService implements DevLoginUseCase {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final FlowerShopRepository flowerShopRepository;
    private final JwtProvider jwtProvider;
    private final JwtProperties jwtProperties;
    private final Clock clock;

    @Override
    @Transactional
    public KakaoLoginResult devLogin(String role) {
        UserRole userRole = UserRole.valueOf(role);

        User user = userRepository.findFirstByRole(userRole)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Long buyerId = findBuyerId(user);
        Long sellerId = findSellerId(user);

        // SELLER인 경우 flower_shop이 있는 유저를 우선 선택
        if (userRole == UserRole.SELLER
                && (sellerId == null || flowerShopRepository.findBySellerId(sellerId).isEmpty())) {
            var betterUser = findSellerWithShop();
            if (betterUser != null) {
                user = betterUser;
                buyerId = null;
                sellerId = findSellerId(user);
            }
        }

        String accessToken = jwtProvider.generateAccessToken(
                user.getId(), userRole.name(), buyerId, sellerId);
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        LocalDateTime refreshExpiresAt = LocalDateTime.now(clock)
                .plusSeconds(jwtProperties.refreshTokenValidityMs() / 1000);
        user.updateRefreshToken(refreshToken, refreshExpiresAt);
        userRepository.save(user);

        boolean hasFlowerShop = false;
        if (userRole == UserRole.SELLER && sellerId != null) {
            hasFlowerShop = flowerShopRepository.findBySellerId(sellerId).isPresent();
        }

        return new KakaoLoginResult(accessToken, refreshToken, userRole.name(), false, hasFlowerShop);
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

    private User findSellerWithShop() {
        return flowerShopRepository.findAll().stream()
                .map(FlowerShop::getSellerId)
                .map(sellerRepository::findById)
                .flatMap(java.util.Optional::stream)
                .map(seller -> userRepository.findById(seller.getUserId()))
                .flatMap(java.util.Optional::stream)
                .filter(u -> u.getRole() == UserRole.SELLER)
                .findFirst()
                .orElse(null);
    }
}
