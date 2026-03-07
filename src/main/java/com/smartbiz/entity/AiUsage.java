package com.smartbiz.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ai_usages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_id")
    private Integer aiId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    private String feature;

    @Column(columnDefinition = "TEXT")
    private String prompt;

    @Column(columnDefinition = "TEXT")
    private String response;

    private LocalDateTime date;
}
