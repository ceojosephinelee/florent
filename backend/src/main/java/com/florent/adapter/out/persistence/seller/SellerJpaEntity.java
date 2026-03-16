package com.florent.adapter.out.persistence.seller;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.florent.domain.seller.Seller;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seller")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class SellerJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    public static SellerJpaEntity from(Seller domain) {
        SellerJpaEntity entity = new SellerJpaEntity();
        entity.id = domain.getId();
        entity.userId = domain.getUserId();
        return entity;
    }

    public Seller toDomain() {
        return Seller.reconstitute(id, userId);
    }
}