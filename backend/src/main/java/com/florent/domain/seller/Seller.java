package com.florent.domain.seller;

import lombok.Getter;

@Getter
public class Seller {
    private Long id;
    private Long userId;

    private Seller() {}

    public static Seller create(Long userId) {
        Seller seller = new Seller();
        seller.userId = userId;
        return seller;
    }

    public static Seller reconstitute(Long id, Long userId) {
        Seller seller = new Seller();
        seller.id = id;
        seller.userId = userId;
        return seller;
    }
}
