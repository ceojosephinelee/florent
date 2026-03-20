DEVICE_ID := 1027E833-E5C1-4A61-8211-7F9CD82252F6
ENV_FILE  := .env.local

.PHONY: run clean restart pub

## Flutter 앱 실행 (iOS 시뮬레이터, 카카오 키 자동 주입)
run:
	@if [ ! -f $(ENV_FILE) ]; then \
		echo "ERROR: $(ENV_FILE) 파일이 없습니다. 먼저 생성하세요."; \
		exit 1; \
	fi
	@export $$(grep -v '^#' $(ENV_FILE) | xargs) > /dev/null 2>&1 && \
		flutter run -d $(DEVICE_ID) \
			--dart-define=KAKAO_NATIVE_KEY=$$KAKAO_NATIVE_KEY \
			--dart-define=DEV_MODE=true

## 빌드 캐시 정리
clean:
	flutter clean

## clean 후 재실행
restart: clean pub run

## 패키지 의존성 설치
pub:
	flutter pub get
