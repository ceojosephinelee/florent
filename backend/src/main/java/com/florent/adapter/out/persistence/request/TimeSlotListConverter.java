package com.florent.adapter.out.persistence.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.florent.domain.request.SlotKind;
import com.florent.domain.request.TimeSlot;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;
import java.util.Map;

@Converter
public class TimeSlotListConverter implements AttributeConverter<List<TimeSlot>, String> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<TimeSlot> attribute) {
        try {
            List<Map<String, String>> raw = attribute.stream()
                    .map(slot -> Map.of("kind", slot.kind().name(), "value", slot.value()))
                    .toList();
            return MAPPER.writeValueAsString(raw);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("List<TimeSlot> → JSON 변환 실패", e);
        }
    }

    @Override
    public List<TimeSlot> convertToEntityAttribute(String dbData) {
        try {
            List<Map<String, String>> raw = MAPPER.readValue(dbData, new TypeReference<>() {});
            return raw.stream()
                    .map(map -> new TimeSlot(SlotKind.valueOf(map.get("kind")), map.get("value")))
                    .toList();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("JSON → List<TimeSlot> 변환 실패", e);
        }
    }
}
