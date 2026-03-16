package com.florent.adapter.out.persistence.shop;

import com.florent.domain.shop.FlowerShop;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "flower_shop")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class FlowerShopJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long sellerId;

    @Column(nullable = false)
    private String name;

    private String description;

    private String phone;

    @Column(nullable = false)
    private String addressText;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lat;

    @Column(nullable = false, precision = 9, scale = 6)
    private BigDecimal lng;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static FlowerShopJpaEntity from(FlowerShop domain) {
        FlowerShopJpaEntity entity = new FlowerShopJpaEntity();
        entity.id = domain.getId();
        entity.sellerId = domain.getSellerId();
        entity.name = domain.getShopName();
        entity.description = null;
        entity.phone = domain.getShopPhone();
        entity.addressText = domain.getShopAddress();
        entity.lat = domain.getShopLat();
        entity.lng = domain.getShopLng();
        return entity;
    }

    public FlowerShop toDomain() {
        return FlowerShop.reconstitute(
                id, sellerId, name, phone, addressText, lat, lng
        );
    }
}
