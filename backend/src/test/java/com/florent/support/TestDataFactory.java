package com.florent.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

@Component
public class TestDataFactory {

    private final JdbcTemplate jdbcTemplate;
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public TestDataFactory(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public String createBuyerAndGetToken(String name) {
        jdbcTemplate.update(
                "INSERT INTO \"user\" (kakao_id, role, created_at, updated_at) "
                + "VALUES (?, 'BUYER', now(), now())", "kakao_" + name);
        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM \"user\" WHERE kakao_id = ?", Long.class, "kakao_" + name);

        jdbcTemplate.update(
                "INSERT INTO buyer (user_id, nick_name, created_at, updated_at) "
                + "VALUES (?, ?, now(), now())", userId, name);
        Long buyerId = jdbcTemplate.queryForObject(
                "SELECT id FROM buyer WHERE user_id = ?", Long.class, userId);

        return TestTokenProvider.createBuyerToken(userId, buyerId);
    }

    public String createSellerAndGetToken(String name) {
        jdbcTemplate.update(
                "INSERT INTO \"user\" (kakao_id, role, created_at, updated_at) "
                + "VALUES (?, 'SELLER', now(), now())", "kakao_seller_" + name);
        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM \"user\" WHERE kakao_id = ?", Long.class, "kakao_seller_" + name);

        jdbcTemplate.update(
                "INSERT INTO seller (user_id, created_at, updated_at) "
                + "VALUES (?, now(), now())", userId);
        Long sellerId = jdbcTemplate.queryForObject(
                "SELECT id FROM seller WHERE user_id = ?", Long.class, userId);

        return TestTokenProvider.createSellerToken(userId, sellerId);
    }

    public Long createSellerWithShop(String shopName, BigDecimal lat, BigDecimal lng) {
        jdbcTemplate.update(
                "INSERT INTO \"user\" (kakao_id, role, created_at, updated_at) "
                + "VALUES (?, 'SELLER', now(), now())", "kakao_shop_" + shopName);
        Long userId = jdbcTemplate.queryForObject(
                "SELECT id FROM \"user\" WHERE kakao_id = ?", Long.class, "kakao_shop_" + shopName);

        jdbcTemplate.update(
                "INSERT INTO seller (user_id, created_at, updated_at) "
                + "VALUES (?, now(), now())", userId);
        Long sellerId = jdbcTemplate.queryForObject(
                "SELECT id FROM seller WHERE user_id = ?", Long.class, userId);

        jdbcTemplate.update(
                "INSERT INTO flower_shop (seller_id, name, address_text, lat, lng, created_at, updated_at) "
                + "VALUES (?, ?, '테스트 주소', ?, ?, now(), now())",
                sellerId, shopName, lat, lng);

        return sellerId;
    }

    public String validRequestBody() {
        return buildRequestBody(
                new String[]{"생일"}, new String[]{"친구"}, new String[]{"밝음"},
                "TIER2", "PICKUP",
                LocalDate.now().plusDays(3).toString(),
                "PICKUP_30M:14:00",
                "서울시 강남구 테헤란로 1",
                "37.498095", "127.027610");
    }

    public String requestBodyFromDataTable(Map<String, String> data) {
        String[] purposeTags = splitCsv(data.getOrDefault("purposeTags", "생일"));
        String[] relationTags = splitCsv(data.getOrDefault("relationTags", "친구"));
        String[] moodTags = splitCsv(data.getOrDefault("moodTags", "밝음"));
        String budgetTier = data.getOrDefault("budgetTier", "TIER2");
        String fulfillmentType = data.getOrDefault("fulfillmentType", "PICKUP");
        String fulfillmentDate = parseFulfillmentDate(
                data.getOrDefault("fulfillmentDate", "오늘로부터 3일 후"));
        String timeSlots = data.getOrDefault("timeSlots", "PICKUP_30M:14:00");
        String placeAddressText = data.getOrDefault("placeAddressText", "서울시 강남구 테헤란로 1");
        String placeLat = data.getOrDefault("placeLat", "37.498095");
        String placeLng = data.getOrDefault("placeLng", "127.027610");

        return buildRequestBody(purposeTags, relationTags, moodTags,
                budgetTier, fulfillmentType, fulfillmentDate, timeSlots,
                placeAddressText, placeLat, placeLng);
    }

    public String requestBodyWithoutBudgetTier() {
        ObjectNode node = MAPPER.createObjectNode();
        node.set("purposeTags", toArrayNode(new String[]{"생일"}));
        node.set("relationTags", toArrayNode(new String[]{"친구"}));
        node.set("moodTags", toArrayNode(new String[]{"밝음"}));
        node.put("fulfillmentType", "PICKUP");
        node.put("fulfillmentDate", LocalDate.now().plusDays(3).toString());
        node.set("requestedTimeSlots", parseTimeSlots("PICKUP_30M:14:00"));
        node.put("placeAddressText", "서울시 강남구 테헤란로 1");
        node.put("placeLat", new BigDecimal("37.498095"));
        node.put("placeLng", new BigDecimal("127.027610"));
        return node.toString();
    }

    public String requestBodyForPickup(double lat, double lng) {
        return buildRequestBody(
                new String[]{"생일"}, new String[]{"친구"}, new String[]{"밝음"},
                "TIER2", "PICKUP",
                LocalDate.now().plusDays(3).toString(),
                "PICKUP_30M:14:00",
                "서울시 강남구 테헤란로 1",
                String.valueOf(lat), String.valueOf(lng));
    }

    public String requestBodyWithTimeSlots(String slotsStr) {
        return buildRequestBody(
                new String[]{"생일"}, new String[]{"친구"}, new String[]{"밝음"},
                "TIER2", "PICKUP",
                LocalDate.now().plusDays(3).toString(),
                slotsStr,
                "서울시 강남구 테헤란로 1",
                "37.498095", "127.027610");
    }

    private String buildRequestBody(String[] purposeTags, String[] relationTags,
                                    String[] moodTags, String budgetTier,
                                    String fulfillmentType, String fulfillmentDate,
                                    String timeSlots, String placeAddressText,
                                    String placeLat, String placeLng) {
        ObjectNode node = MAPPER.createObjectNode();
        node.set("purposeTags", toArrayNode(purposeTags));
        node.set("relationTags", toArrayNode(relationTags));
        node.set("moodTags", toArrayNode(moodTags));
        node.put("budgetTier", budgetTier);
        node.put("fulfillmentType", fulfillmentType);
        node.put("fulfillmentDate", fulfillmentDate);
        node.set("requestedTimeSlots", parseTimeSlots(timeSlots));
        node.put("placeAddressText", placeAddressText);
        node.put("placeLat", new BigDecimal(placeLat));
        node.put("placeLng", new BigDecimal(placeLng));
        return node.toString();
    }

    private ArrayNode parseTimeSlots(String slotsStr) {
        ArrayNode array = MAPPER.createArrayNode();
        String[] slots = slotsStr.split(",\\s*");
        for (String slot : slots) {
            String[] parts = slot.trim().split(":", 2);
            ObjectNode slotNode = MAPPER.createObjectNode();
            slotNode.put("kind", parts[0]);
            slotNode.put("value", parts[1]);
            array.add(slotNode);
        }
        return array;
    }

    private ArrayNode toArrayNode(String[] values) {
        ArrayNode array = MAPPER.createArrayNode();
        for (String value : values) {
            array.add(value.trim());
        }
        return array;
    }

    private String[] splitCsv(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .toArray(String[]::new);
    }

    public Long createOpenRequest(Long buyerId) {
        String timeSlotsJson = "[{\"kind\":\"PICKUP_30M\",\"value\":\"14:00\"}]";
        jdbcTemplate.update(
                "INSERT INTO curation_request "
                + "(buyer_id, status, purpose_tags_json, relation_tags_json, mood_tags_json, "
                + "budget_tier, fulfillment_type, fulfillment_date, requested_time_slots_json, "
                + "place_address_text, place_lat, place_lng, created_at, expires_at, updated_at) "
                + "VALUES (?, 'OPEN', '[\"생일\"]', '[\"친구\"]', '[\"밝음\"]', "
                + "'TIER2', 'PICKUP', ?, ?, "
                + "'서울시 강남구 테헤란로 1', 37.498095, 127.027610, now(), now() + INTERVAL '48' HOUR, now())",
                buyerId,
                LocalDate.now().plusDays(3).toString(),
                timeSlotsJson);
        return jdbcTemplate.queryForObject(
                "SELECT id FROM curation_request WHERE buyer_id = ? ORDER BY id DESC LIMIT 1",
                Long.class, buyerId);
    }

    public void createProposal(Long requestId, Long flowerShopId, String status) {
        jdbcTemplate.update(
                "INSERT INTO proposal "
                + "(request_id, flower_shop_id, status, description, "
                + "available_slot_kind, available_slot_value, price, "
                + "created_at, expires_at, updated_at) "
                + "VALUES (?, ?, ?, '테스트 제안 설명', "
                + "'PICKUP_30M', '14:00', 30000, "
                + "now(), now() + INTERVAL '24' HOUR, now())",
                requestId, flowerShopId, status);
    }

    public Long createProposalAndGetId(Long requestId, Long flowerShopId, String status) {
        String expiresAtExpr = "EXPIRED".equals(status)
                ? "now() - INTERVAL '1' HOUR"
                : "now() + INTERVAL '24' HOUR";
        jdbcTemplate.update(
                "INSERT INTO proposal "
                + "(request_id, flower_shop_id, status, concept_title, description, "
                + "available_slot_kind, available_slot_value, price, "
                + "created_at, expires_at, submitted_at, updated_at) "
                + "VALUES (?, ?, ?, '테스트 컨셉', '테스트 제안 설명', "
                + "'PICKUP_30M', '14:00', 35000, "
                + "now() - INTERVAL '25' HOUR, " + expiresAtExpr + ", "
                + "CASE WHEN ? = 'SUBMITTED' THEN now() ELSE NULL END, now())",
                requestId, flowerShopId, status, status);
        return jdbcTemplate.queryForObject(
                "SELECT id FROM proposal WHERE request_id = ? AND flower_shop_id = ?",
                Long.class, requestId, flowerShopId);
    }

    public Long getBuyerIdFromToken(String token) {
        try {
            String json = new String(java.util.Base64.getDecoder().decode(token));
            return MAPPER.readTree(json).get("buyerId").asLong();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String parseFulfillmentDate(String text) {
        if (text.contains("오늘로부터") && text.contains("일 후")) {
            String daysStr = text.replaceAll("[^0-9]", "");
            int days = Integer.parseInt(daysStr);
            return LocalDate.now().plusDays(days).toString();
        }
        return text;
    }
}
