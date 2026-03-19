package com.florent.adapter.in.address;

public record AddressSearchResponse(
        String addressName,
        double lat,
        double lng
) {
}
