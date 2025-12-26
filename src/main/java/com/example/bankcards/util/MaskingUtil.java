package com.example.bankcards.util;

public class MaskingUtil {
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
        // Добавим одну строку
        //создали новую ветку "second-branch" и написали текущую строку
    }
}