-- 이메일 가입 사용자는 kakao_id가 없으므로 nullable로 변경
ALTER TABLE "user" ALTER COLUMN kakao_id DROP NOT NULL;

-- 이메일 유니크 인덱스 추가 (null 허용, 값이 있으면 유니크)
CREATE UNIQUE INDEX idx_user_email_unique ON "user" (email) WHERE email IS NOT NULL;
