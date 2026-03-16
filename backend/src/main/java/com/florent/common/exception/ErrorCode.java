package com.florent.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    UNAUTHORIZED(401, "인증이 필요합니다."),
    TOKEN_EXPIRED(401, "토큰이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED(401, "리프레시 토큰이 만료되었습니다. 재로그인이 필요합니다."),
    FORBIDDEN(403, "접근 권한이 없습니다."),
    VALIDATION_ERROR(400, "요청 값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),

    // Request
    REQUEST_NOT_FOUND(404, "요청을 찾을 수 없습니다."),
    REQUEST_NOT_OPEN(422, "진행 중인 요청이 아닙니다."),
    REQUEST_ALREADY_CONFIRMED(422, "이미 확정된 요청입니다."),

    // Proposal
    PROPOSAL_NOT_FOUND(404, "제안을 찾을 수 없습니다."),
    PROPOSAL_ALREADY_EXISTS(422, "이미 제안서를 작성했습니다."),
    PROPOSAL_NOT_SUBMITTABLE(422, "제출할 수 없는 상태입니다."),
    PROPOSAL_NOT_EDITABLE(422, "수정할 수 없는 상태입니다."),
    PROPOSAL_NOT_SELECTABLE(422, "선택할 수 없는 상태입니다."),
    PROPOSAL_EXPIRED(422, "만료된 제안입니다."),

    // Reservation
    RESERVATION_NOT_FOUND(404, "예약을 찾을 수 없습니다."),

    // Payment
    DUPLICATE_PAYMENT(422, "중복 결제 요청입니다."),

    // Notification
    NOTIFICATION_NOT_FOUND(404, "알림을 찾을 수 없습니다."),
    NOTIFICATION_ALREADY_READ(422, "이미 읽은 알림입니다."),

    // Shop
    SHOP_NOT_FOUND(404, "꽃집을 찾을 수 없습니다."),
    SHOP_ALREADY_EXISTS(422, "이미 꽃집이 등록되어 있습니다.");

    private final int httpStatus;
    private final String message;
}
