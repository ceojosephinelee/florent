package com.florent.application.seller;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import com.florent.domain.shop.GetShopUseCase;
import com.florent.domain.shop.RegisterShopCommand;
import com.florent.domain.shop.RegisterShopResult;
import com.florent.domain.shop.RegisterShopUseCase;
import com.florent.domain.shop.ShopDetailResult;
import com.florent.domain.shop.UpdateShopCommand;
import com.florent.domain.shop.UpdateShopUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerShopService implements RegisterShopUseCase, GetShopUseCase, UpdateShopUseCase {

    private final FlowerShopRepository flowerShopRepository;

    @Override
    @Transactional
    public RegisterShopResult register(Long sellerId, RegisterShopCommand command) {
        if (flowerShopRepository.findBySellerId(sellerId).isPresent()) {
            throw new BusinessException(ErrorCode.SHOP_ALREADY_EXISTS);
        }

        FlowerShop shop = FlowerShop.create(
                sellerId,
                command.name(),
                command.description(),
                command.phone(),
                command.addressText(),
                command.lat(),
                command.lng());
        FlowerShop saved = flowerShopRepository.save(shop);

        return new RegisterShopResult(saved.getId(), saved.getShopName());
    }

    @Override
    @Transactional(readOnly = true)
    public ShopDetailResult getShop(Long sellerId) {
        FlowerShop shop = flowerShopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));
        return ShopDetailResult.from(shop);
    }

    @Override
    @Transactional
    public ShopDetailResult update(Long sellerId, UpdateShopCommand command) {
        FlowerShop shop = flowerShopRepository.findBySellerId(sellerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHOP_NOT_FOUND));

        shop.update(
                command.name(),
                command.description(),
                command.phone(),
                command.addressText(),
                command.lat(),
                command.lng());
        FlowerShop saved = flowerShopRepository.save(shop);

        return ShopDetailResult.from(saved);
    }
}
