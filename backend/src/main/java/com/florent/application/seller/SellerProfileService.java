package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.seller.GetSellerProfileUseCase;
import com.florent.domain.seller.Seller;
import com.florent.domain.seller.SellerProfileResult;
import com.florent.domain.seller.SellerRepository;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerProfileService implements GetSellerProfileUseCase {

    private final SellerRepository sellerRepository;
    private final FlowerShopRepository flowerShopRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public SellerProfileResult getProfile(Long sellerId) {
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User user = userRepository.findById(seller.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        FlowerShop shop = flowerShopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        return new SellerProfileResult(
                sellerId,
                shop.getShopName(),
                shop.getShopAddress(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
