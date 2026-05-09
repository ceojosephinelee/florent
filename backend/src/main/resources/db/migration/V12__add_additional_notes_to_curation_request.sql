-- V12__add_additional_notes_to_curation_request.sql
-- 구매자 요청 시 추가 요청사항 입력란 (선택)

ALTER TABLE curation_request
    ADD COLUMN additional_notes VARCHAR(500) NULL;
