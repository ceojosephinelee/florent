-- 판매자별 제안 목록 조회 성능 개선
CREATE INDEX idx_proposal_flower_shop_id_created_at
  ON proposal (flower_shop_id, created_at DESC);

-- sellerId → FlowerShop 조회 성능 개선
CREATE INDEX idx_flower_shop_seller_id ON flower_shop (seller_id);

-- V5에서 DROP된 price CHECK 제약 복구 (NULL 허용)
ALTER TABLE proposal ADD CONSTRAINT proposal_price_positive
  CHECK (price IS NULL OR price > 0);
