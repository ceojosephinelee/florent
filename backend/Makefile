.PHONY: run stop test clean

## 로컬 개발 서버 실행 (local 프로파일)
run:
	@set -a; . ./.env.local; set +a; \
	./gradlew bootRun --args='--spring.profiles.active=local'

## 8080 포트 프로세스 종료
stop:
	@PID=$$(lsof -ti :8080); \
	if [ -n "$$PID" ]; then \
		kill $$PID && echo "Stopped process $$PID on port 8080"; \
	else \
		echo "No process found on port 8080"; \
	fi

## 전체 테스트 실행
test:
	./gradlew test

## 빌드 캐시 정리
clean:
	./gradlew clean
