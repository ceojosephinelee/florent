package com.florent.application.buyer;

import com.florent.domain.request.BudgetTier;
import com.florent.domain.request.CreateRequestCommand;
import com.florent.domain.request.CreateRequestResult;
import com.florent.domain.request.FulfillmentType;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import com.florent.domain.shop.FlowerShop;
import com.florent.fake.FakeCurationRequestRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeSaveNotificationPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class BuyerRequestServiceTest {

    private FakeCurationRequestRepository requestRepository;
    private FakeFlowerShopRepository shopRepository;
    private FakeSaveNotificationPort notificationPort;
    private BuyerRequestService sut;

    @BeforeEach
    void setUp() {
        requestRepository = new FakeCurationRequestRepository();
        shopRepository = new FakeFlowerShopRepository();
        notificationPort = new FakeSaveNotificationPort();
        sut = new BuyerRequestService(requestRepository, shopRepository, notificationPort);
    }

    private CreateRequestCommand defaultCommand() {
        return new CreateRequestCommand(
                1L,
                List.of("생일"),
                List.of("연인"),
                List.of("로맨틱"),
                BudgetTier.TIER2,
                FulfillmentType.DELIVERY,
                LocalDate.now().plusDays(3),
                List.of(new TimeSlot(SlotKind.DELIVERY_WINDOW, "14:00-16:00")),
                "서울시 강남구",
                new BigDecimal("37.498095"),
                new BigDecimal("127.027610")
        );
    }

    @Test
    @DisplayName("요청이 저장되고 결과가 반환된다")
    void 요청_저장_확인() {
        // given
        CreateRequestCommand command = defaultCommand();

        // when
        CreateRequestResult result = sut.create(command);

        // then
        assertThat(result.requestId()).isNotNull();
        assertThat(result.status()).isEqualTo("OPEN");
        assertThat(requestRepository.findById(result.requestId())).isPresent();
    }

    @Test
    @DisplayName("반경 2km 내 꽃집에만 알림이 발송된다")
    void 반경_내_꽃집에만_알림_발송() {
        // given — 기준점: 37.498095, 127.027610
        // 약 1km 거리 꽃집
        FlowerShop nearbyShop = FlowerShop.reconstitute(
                1L, 10L, "가까운꽃집", "서울시 강남구",
                new BigDecimal("37.505"),
                new BigDecimal("127.027"));
        // 약 10km 거리 꽃집
        FlowerShop farShop = FlowerShop.reconstitute(
                2L, 20L, "먼꽃집", "서울시 마포구",
                new BigDecimal("37.560"),
                new BigDecimal("126.920"));
        shopRepository.save(nearbyShop);
        shopRepository.save(farShop);

        // when
        sut.create(defaultCommand());

        // then
        assertThat(notificationPort.getRecords()).hasSize(1);
        assertThat(notificationPort.getRecords().get(0).sellerId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("반경 밖 꽃집에는 알림이 발송되지 않는다")
    void 반경_밖_꽃집_알림_미발송() {
        // given — 모든 꽃집이 반경 밖
        FlowerShop farShop = FlowerShop.reconstitute(
                1L, 10L, "먼꽃집", "서울시 마포구",
                new BigDecimal("37.560"),
                new BigDecimal("126.920"));
        shopRepository.save(farShop);

        // when
        sut.create(defaultCommand());

        // then
        assertThat(notificationPort.getRecords()).isEmpty();
    }

    @Test
    @DisplayName("꽃집이 0개일 때 정상 완료된다")
    void 꽃집_0개일_때_정상_완료() {
        // given — 꽃집 없음

        // when
        CreateRequestResult result = sut.create(defaultCommand());

        // then
        assertThat(result.requestId()).isNotNull();
        assertThat(notificationPort.getRecords()).isEmpty();
    }
}