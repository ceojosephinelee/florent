package com.florent.application.auth;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.domain.auth.WithdrawUseCase;
import com.florent.domain.buyer.BuyerRepository;
import com.florent.domain.notification.NotificationRepository;
import com.florent.domain.notification.OutboxEventRepository;
import com.florent.domain.notification.UserDeviceRepository;
import com.florent.domain.payment.PaymentRepository;
import com.florent.domain.proposal.ProposalRepository;
import com.florent.domain.request.CurationRequestRepository;
import com.florent.domain.reservation.ReservationRepository;
import com.florent.domain.seller.SellerRepository;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.shop.FlowerShopRepository;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRepository;
import com.florent.domain.user.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WithdrawService implements WithdrawUseCase {

    private final UserRepository userRepository;
    private final BuyerRepository buyerRepository;
    private final SellerRepository sellerRepository;
    private final FlowerShopRepository flowerShopRepository;
    private final CurationRequestRepository curationRequestRepository;
    private final ProposalRepository proposalRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final NotificationRepository notificationRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final UserDeviceRepository userDeviceRepository;

    @Override
    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() == UserRole.BUYER) {
            deleteBuyerData(userId);
        } else if (user.getRole() == UserRole.SELLER) {
            deleteSellerData(userId);
        }

        deleteCommonData(userId);
        userRepository.deleteById(userId);
        log.info("회원 탈퇴 완료: userId={}", userId);
    }

    private void deleteBuyerData(Long userId) {
        buyerRepository.findByUserId(userId).ifPresent(buyer -> {
            List<Long> requestIds = curationRequestRepository.findIdsByBuyerId(buyer.getId());
            if (!requestIds.isEmpty()) {
                deleteRequestCascade(requestIds);
                curationRequestRepository.deleteByBuyerId(buyer.getId());
            }
            buyerRepository.deleteByUserId(userId);
        });
    }

    private void deleteSellerData(Long userId) {
        sellerRepository.findByUserId(userId).ifPresent(seller -> {
            flowerShopRepository.findBySellerId(seller.getId()).ifPresent(shop ->
                    deleteFlowerShopCascade(shop));
            flowerShopRepository.deleteBySellerId(seller.getId());
            sellerRepository.deleteByUserId(userId);
        });
    }

    private void deleteRequestCascade(List<Long> requestIds) {
        List<Long> proposalIds = proposalRepository.findIdsByRequestIds(requestIds);
        if (!proposalIds.isEmpty()) {
            List<Long> reservationIds = reservationRepository.findIdsByRequestIds(requestIds);
            if (!reservationIds.isEmpty()) {
                paymentRepository.deleteByReservationIds(reservationIds);
                reservationRepository.deleteByRequestIds(requestIds);
            }
            proposalRepository.deleteByRequestIds(requestIds);
        }
    }

    private void deleteFlowerShopCascade(FlowerShop shop) {
        proposalRepository.deleteByFlowerShopId(shop.getId());
    }

    private void deleteCommonData(Long userId) {
        List<Long> notificationIds = notificationRepository.findIdsByUserId(userId);
        if (!notificationIds.isEmpty()) {
            outboxEventRepository.deleteByNotificationIds(notificationIds);
            notificationRepository.deleteByUserId(userId);
        }
        userDeviceRepository.deleteByUserId(userId);
    }
}
