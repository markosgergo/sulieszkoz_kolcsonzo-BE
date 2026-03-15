package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception;

// 400 - amikor az uzleti logika tilt egy muveletet
// pl. egy mar visszaadott eszkoz ujboli visszavétele, vagy nem elerheto eszkoz kolcsonzese
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}