package com.smartbiz.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "subscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Integer subscriptionId;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "duration_days")
    private Integer durationDays;

    @OneToMany(mappedBy = "subscription", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Business> businesses;
}
