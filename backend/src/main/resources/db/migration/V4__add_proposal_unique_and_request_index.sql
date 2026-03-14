-- V4__add_proposal_unique_and_request_index.sql
-- 1) proposal 테이블: 동일 요청에 같은 꽃집이 중복 제안 방지
-- 2) curation_request 테이블: 구매자별 최신순 조회 성능 개선

ALTER TABLE proposal
    ADD CONSTRAINT uq_proposal_request_shop UNIQUE (request_id, flower_shop_id);

CREATE INDEX idx_curation_request_buyer_created
    ON curation_request (buyer_id, created_at DESC);