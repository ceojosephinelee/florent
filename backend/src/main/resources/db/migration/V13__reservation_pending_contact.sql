-- V13__reservation_pending_contact.sql
-- 결제 플로우 제거 → 판매자 연락 + 예약 확정 플로우 전환
-- PENDING_CONTACT 상태 추가를 위해 confirmed_at nullable로 변경
-- 기존 CONFIRMED 데이터는 이미 완료된 예약이므로 그대로 유지

ALTER TABLE reservation ALTER COLUMN confirmed_at DROP NOT NULL;
