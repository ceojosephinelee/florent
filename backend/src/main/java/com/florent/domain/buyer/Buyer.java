package com.florent.domain.buyer;

import lombok.Getter;

@Getter
public class Buyer {
    private Long id;
    private Long userId;
    private String nickName;

    private Buyer() {}

    public static Buyer reconstitute(Long id, Long userId, String nickName) {
        Buyer b = new Buyer();
        b.id = id;
        b.userId = userId;
        b.nickName = nickName;
        return b;
    }
}
