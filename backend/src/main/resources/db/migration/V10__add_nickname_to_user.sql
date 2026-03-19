-- V10__add_nickname_to_user.sql
-- 카카오 로그인 시 받아온 닉네임을 user 테이블에 저장

ALTER TABLE "user" ADD COLUMN nickname VARCHAR(255);
