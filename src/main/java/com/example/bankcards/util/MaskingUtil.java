package com.example.bankcards.util;


// Смотри, у тебя **** только в дто выходят, но что ты будешь делать с логами?
// 2 варианта - кастомный toSting (в лоб) и красивый вариант с настройкой файла логирования
// https://www.baeldung.com/logback-mask-sensitive-data - тут пример
// и тут еще https://stackoverflow.com/questions/16775253/how-masking-of-sensitive-data-is-achieved-using-slf4j-framework
public class MaskingUtil {
    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}