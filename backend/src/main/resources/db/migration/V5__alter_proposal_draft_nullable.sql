-- V5__alter_proposal_draft_nullable.sql
-- DRAFT 생성 시 description, available_slot_kind, available_slot_value, price가 비어있을 수 있으므로
-- NOT NULL 제약 해제. 제출 시 Java 레벨에서 필수 검증.

ALTER TABLE proposal ALTER COLUMN description DROP NOT NULL;
ALTER TABLE proposal ALTER COLUMN available_slot_kind DROP NOT NULL;
ALTER TABLE proposal ALTER COLUMN available_slot_value DROP NOT NULL;
ALTER TABLE proposal ALTER COLUMN price DROP NOT NULL;
ALTER TABLE proposal DROP CONSTRAINT IF EXISTS proposal_price_check;
