package com.example.bankcards.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "transfer")
public class TransferEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "TransferTime", nullable = false)
    private LocalDateTime transferTime;

    @JoinColumn(name = "from_card_id", nullable = false)
    private Long fromCardIdEntity;

    @JoinColumn(name = "to_card_id", nullable = false)
    private Long toCardIdEntity;

    @Column(name = "amount", nullable = false)
    @Positive
    private BigDecimal amount;
}
