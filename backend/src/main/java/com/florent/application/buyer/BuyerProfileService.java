package com.florent.application.buyer;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.buyer.Buyer;
import com.florent.domain.buyer.BuyerProfileResult;
import com.florent.domain.buyer.BuyerRepository;
import com.florent.domain.buyer.GetBuyerProfileUseCase;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BuyerProfileService implements GetBuyerProfileUseCase {

    private final BuyerRepository buyerRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public BuyerProfileResult getProfile(Long buyerId, Long userId) {
        Buyer buyer = buyerRepository.findById(buyerId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return new BuyerProfileResult(
                buyer.getId(),
                buyer.getNickName(),
                user.getEmail(),
                user.getRole().name(),
                user.getCreatedAt()
        );
    }
}
