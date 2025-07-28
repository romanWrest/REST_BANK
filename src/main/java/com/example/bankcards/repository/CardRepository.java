package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import com.example.bankcards.entity.enums.CardStatus;
import com.example.bankcards.entity.enums.Banks;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUserId(Long userId);

    Page<Card> findByUserId(Long userId, Pageable pageable);

    Page<Card> findByUserIdAndStatus(Long userId, CardStatus status, Pageable pageable);

    Page<Card> findByUserIdAndBank(Long userId, Banks bank, Pageable pageable);
}
