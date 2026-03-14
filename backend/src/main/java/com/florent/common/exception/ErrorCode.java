package com.florent.common.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // Request
    REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "요청을 찾을 수 없습니다."),
    REQUEST_NOT_OPEN(HttpStatus.UNPROCESSABLE_ENTITY, "진행 중인 요청이 아닙니다."),
    REQUEST_ALREADY_CONFIRMED(HttpStatus.UNPROCESSABLE_ENTITY, "이미 확정된 요청입니다."),

    // Proposal
    PROPOSAL_NOT_FOUND(HttpStatus.NOT_FOUND, "제안을 찾을 수 없습니다."),
    PROPOSAL_ALREADY_EXISTS(HttpStatus.UNPROCESSABLE_ENTITY, "이미 제안서를 작성했습니다."),
    PROPOSAL_NOT_SUBMITTABLE(HttpStatus.UNPROCESSABLE_ENTITY, "제출할 수 없는 상태입니다."),
    PROPOSAL_EXPIRED(HttpStatus.UNPROCESSABLE_ENTITY, "만료된 제안입니다."),

    // Payment
    DUPLICATE_PAYMENT(HttpStatus.UNPROCESSABLE_ENTITY, "중복 결제 요청입니다."),

    // Shop
    SHOP_NOT_FOUND(HttpStatus.NOT_FOUND, "꽃집을 찾을 수 없습니다."),
    SHOP_ALREADY_EXISTS(HttpStatus.UNPROCESSABLE_ENTITY, "이미 꽃집이 등록되어 있습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
