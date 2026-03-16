package com.florent.support;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.cucumber.spring.ScenarioScope;

import java.util.HashMap;
import java.util.Map;

@Component
@ScenarioScope
public class ScenarioContext {
    private ResponseEntity<String> response;
    private String buyerToken;
    private String sellerToken;
    private Long requestId;
    private Long proposalId;
    private Long reservationId;
    private Long otherProposalId;
    private Long notificationId;
    private final Map<String, Long> otherBuyerRequestIds = new HashMap<>();
    private final Map<String, String> sellerTokens = new HashMap<>();
    private final Map<String, String> buyerTokens = new HashMap<>();

    public ResponseEntity<String> getResponse() {
        return response;
    }

    public void setResponse(ResponseEntity<String> response) {
        this.response = response;
    }

    public String getBuyerToken() {
        return buyerToken;
    }

    public void setBuyerToken(String buyerToken) {
        this.buyerToken = buyerToken;
    }

    public String getSellerToken() {
        return sellerToken;
    }

    public void setSellerToken(String sellerToken) {
        this.sellerToken = sellerToken;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public void putOtherBuyerRequestId(String name, Long requestId) {
        otherBuyerRequestIds.put(name, requestId);
    }

    public Long getOtherBuyerRequestId(String name) {
        return otherBuyerRequestIds.get(name);
    }

    public void putSellerToken(String name, String token) {
        sellerTokens.put(name, token);
    }

    public String getSellerToken(String name) {
        return sellerTokens.get(name);
    }

    public void putBuyerToken(String name, String token) {
        buyerTokens.put(name, token);
    }

    public String getBuyerToken(String name) {
        return buyerTokens.get(name);
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public Long getOtherProposalId() {
        return otherProposalId;
    }

    public void setOtherProposalId(Long otherProposalId) {
        this.otherProposalId = otherProposalId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }
}
