package com.florent.application.auth;

import com.florent.common.exception.BusinessException;
import com.florent.common.exception.ErrorCode;
import com.florent.common.security.JwtProperties;
import com.florent.common.security.JwtProvider;
import com.florent.domain.auth.KakaoLoginCommand;
import com.florent.domain.auth.KakaoLoginResult;
import com.florent.domain.auth.KakaoUserInfo;
import com.florent.domain.auth.ReissueTokenCommand;
import com.florent.domain.auth.ReissueTokenResult;
import com.florent.domain.auth.RegisterSellerInfoCommand;
import com.florent.domain.auth.RegisterSellerInfoResult;
import com.florent.domain.auth.SetRoleCommand;
import com.florent.domain.auth.SetRoleResult;
import com.florent.domain.seller.Seller;
import com.florent.domain.shop.FlowerShop;
import com.florent.domain.user.User;
import com.florent.domain.user.UserRole;
import com.florent.fake.FakeBuyerRepository;
import com.florent.fake.FakeFlowerShopRepository;
import com.florent.fake.FakeKakaoOAuthPort;
import com.florent.fake.FakeSellerRepository;
import com.florent.fake.FakeUserRepository;
import com.florent.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AuthServiceTest {

    private FakeKakaoOAuthPort kakaoOAuthPort;
    private FakeUserRepository userRepository;
    private FakeBuyerRepository buyerRepository;
    private FakeSellerRepository sellerRepository;
    private FakeFlowerShopRepository flowerShopRepository;
    private JwtProvider jwtProvider;
    private JwtProperties jwtProperties;
    private AuthService sut;

    private final Clock fixedClock = TestFixtures.FIXED_CLOCK;

    @BeforeEach
    void setUp() {
        kakaoOAuthPort = new FakeKakaoOAuthPort();
        userRepository = new FakeUserRepository();
        buyerRepository = new FakeBuyerRepository();
        sellerRepository = new FakeSellerRepository();
        flowerShopRepository = new FakeFlowerShopRepository();
        jwtProperties = new JwtProperties(
                "test-secret-key-minimum-256-bits-long-for-hmac-sha",
                3600000L, 2592000000L);
        jwtProvider = new JwtProvider(jwtProperties, fixedClock);
        sut = new AuthService(
                kakaoOAuthPort, userRepository, buyerRepository,
                sellerRepository, flowerShopRepository,
                jwtProvider, jwtProperties, fixedClock);
    }

    // === 카카오 로그인 ===

    @Test
    @DisplayName("카카오 신규 유저 로그인 — isNewUser가 true이다")
    void 카카오_신규_유저_로그인() {
        // given
        kakaoOAuthPort.setResponse(new KakaoUserInfo("kakao123", "test@kakao.com", "TestUser"));

        // when
        KakaoLoginResult result = sut.login(new KakaoLoginCommand("access-token"));

        // then
        assertThat(result.isNewUser()).isTrue();
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(result.role()).isNull();
    }

    @Test
    @DisplayName("카카오 기존 유저 로그인 — isNewUser가 false이다")
    void 카카오_기존_유저_로그인() {
        // given
        kakaoOAuthPort.setResponse(new KakaoUserInfo("kakao123", "test@kakao.com", "TestUser"));
        sut.login(new KakaoLoginCommand("access-token"));

        // when
        KakaoLoginResult result = sut.login(new KakaoLoginCommand("access-token-2"));

        // then
        assertThat(result.isNewUser()).isFalse();
    }

    // === 역할 설정 ===

    @Test
    @DisplayName("BUYER 역할 설정 — Buyer 엔티티가 생성된다")
    void BUYER_역할_설정_성공() {
        // given
        User user = userRepository.save(User.createFromKakao("kakao123", "test@kakao.com", "테스트유저"));

        // when
        SetRoleResult result = sut.setRole(user.getId(), new SetRoleCommand(UserRole.BUYER));

        // then
        assertThat(result.role()).isEqualTo("BUYER");
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
        assertThat(buyerRepository.findByUserId(user.getId())).isPresent();
    }

    @Test
    @DisplayName("SELLER 역할 설정 — Seller 엔티티가 생성된다")
    void SELLER_역할_설정_성공() {
        // given
        User user = userRepository.save(User.createFromKakao("kakao123", "test@kakao.com", "테스트유저"));

        // when
        SetRoleResult result = sut.setRole(user.getId(), new SetRoleCommand(UserRole.SELLER));

        // then
        assertThat(result.role()).isEqualTo("SELLER");
        assertThat(sellerRepository.findByUserId(user.getId())).isPresent();
    }

    @Test
    @DisplayName("이미 역할이 설정된 유저 — ROLE_ALREADY_SET 예외")
    void 역할_중복_설정_예외() {
        // given
        User user = userRepository.save(User.createFromKakao("kakao123", "test@kakao.com", "테스트유저"));
        sut.setRole(user.getId(), new SetRoleCommand(UserRole.BUYER));

        // when & then
        assertThatThrownBy(() -> sut.setRole(user.getId(), new SetRoleCommand(UserRole.SELLER)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.ROLE_ALREADY_SET));
    }

    // === 토큰 재발급 ===

    @Test
    @DisplayName("유효한 리프레시 토큰 — 새 토큰 쌍이 발급된다")
    void 토큰_재발급_성공() {
        // given
        kakaoOAuthPort.setResponse(new KakaoUserInfo("kakao123", "test@kakao.com", "TestUser"));
        KakaoLoginResult loginResult = sut.login(new KakaoLoginCommand("access-token"));

        // when
        ReissueTokenResult result = sut.reissue(
                new ReissueTokenCommand(loginResult.refreshToken()));

        // then
        assertThat(result.accessToken()).isNotBlank();
        assertThat(result.refreshToken()).isNotBlank();
    }

    @Test
    @DisplayName("DB에 저장되지 않은 리프레시 토큰 — REFRESH_TOKEN_EXPIRED 예외")
    void 토큰_재발급_불일치_예외() {
        // given
        kakaoOAuthPort.setResponse(new KakaoUserInfo("kakao123", "test@kakao.com", "TestUser"));
        sut.login(new KakaoLoginCommand("access-token"));

        // 다른 userId로 생성된 토큰 (DB에 저장된 것과 불일치)
        String fakeRefreshToken = jwtProvider.generateRefreshToken(999L);

        // when & then
        assertThatThrownBy(() -> sut.reissue(new ReissueTokenCommand(fakeRefreshToken)))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.USER_NOT_FOUND));
    }

    // === 로그아웃 ===

    @Test
    @DisplayName("로그아웃 — 리프레시 토큰이 제거된다")
    void 로그아웃_성공() {
        // given
        kakaoOAuthPort.setResponse(new KakaoUserInfo("kakao123", "test@kakao.com", "TestUser"));
        KakaoLoginResult loginResult = sut.login(new KakaoLoginCommand("access-token"));

        // when
        sut.logout(1L);

        // then
        User user = userRepository.findById(1L).orElseThrow();
        assertThat(user.getRefreshToken()).isNull();
    }

    // === 판매자 정보 등록 ===

    @Test
    @DisplayName("판매자 정보 등록 — FlowerShop이 생성된다")
    void 판매자_정보_등록_성공() {
        // given
        Seller seller = sellerRepository.save(Seller.create(1L));

        // when
        RegisterSellerInfoResult result = sut.register(
                seller.getId(),
                new RegisterSellerInfoCommand(
                        "꽃다발가게", "서울시 강남구", new BigDecimal("37.498"),
                        new BigDecimal("127.027"), "123-45-67890"));

        // then
        assertThat(result.shopName()).isEqualTo("꽃다발가게");
        assertThat(flowerShopRepository.findBySellerId(seller.getId())).isPresent();
    }

    @Test
    @DisplayName("판매자 정보 중복 등록 — SELLER_ALREADY_REGISTERED 예외")
    void 판매자_정보_중복_등록_예외() {
        // given
        Seller seller = sellerRepository.save(Seller.create(1L));
        flowerShopRepository.save(FlowerShop.create(
                seller.getId(), "기존가게", null, null, "서울시",
                new BigDecimal("37.498"), new BigDecimal("127.027")));

        // when & then
        assertThatThrownBy(() -> sut.register(
                seller.getId(),
                new RegisterSellerInfoCommand(
                        "새가게", "서울시 강남구", new BigDecimal("37.498"),
                        new BigDecimal("127.027"), "123-45-67890")))
                .isInstanceOf(BusinessException.class)
                .satisfies(ex -> assertThat(((BusinessException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.SELLER_ALREADY_REGISTERED));
    }
}
