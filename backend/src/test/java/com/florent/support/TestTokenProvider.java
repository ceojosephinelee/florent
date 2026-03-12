package com.florent.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Base64;

public final class TestTokenProvider {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private TestTokenProvider() {}

    public static String createBuyerToken(Long userId, Long buyerId) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("userId", userId);
        node.put("buyerId", buyerId);
        node.put("role", "BUYER");
        return Base64.getEncoder().encodeToString(node.toString().getBytes());
    }

    public static String createSellerToken(Long userId, Long sellerId) {
        ObjectNode node = MAPPER.createObjectNode();
        node.put("userId", userId);
        node.put("sellerId", sellerId);
        node.put("role", "SELLER");
        return Base64.getEncoder().encodeToString(node.toString().getBytes());
    }
}
