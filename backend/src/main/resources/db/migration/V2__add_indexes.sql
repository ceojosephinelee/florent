-- V2__add_indexes.sql
-- 조회 성능 개선을 위한 인덱스 추가

CREATE INDEX idx_curation_request_buyer_id ON curation_request (buyer_id);
CREATE INDEX idx_curation_request_status ON curation_request (status);
CREATE INDEX idx_curation_request_status_expires_at ON curation_request (status, expires_at);
CREATE INDEX idx_flower_shop_lat_lng ON flower_shop (lat, lng);