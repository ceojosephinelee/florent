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
    private final Map<String, Long> otherBuyerRequestIds = new HashMap<>();

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

    public void putOtherBuyerRequestId(String name, Long requestId) {
        otherBuyerRequestIds.put(name, requestId);
    }

    public Long getOtherBuyerRequestId(String name) {
        return otherBuyerRequestIds.get(name);
    }
}
