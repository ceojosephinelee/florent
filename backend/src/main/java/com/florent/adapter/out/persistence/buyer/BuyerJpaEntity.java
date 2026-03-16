package com.florent.adapter.out.persistence.buyer;

import com.florent.domain.buyer.Buyer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "buyer")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class BuyerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    private String nickName;

    public static BuyerJpaEntity from(Buyer domain) {
        BuyerJpaEntity entity = new BuyerJpaEntity();
        entity.id = domain.getId();
        entity.userId = domain.getUserId();
        entity.nickName = domain.getNickName();
        return entity;
    }

    public Buyer toDomain() {
        return Buyer.reconstitute(id, userId, nickName);
    }
}
