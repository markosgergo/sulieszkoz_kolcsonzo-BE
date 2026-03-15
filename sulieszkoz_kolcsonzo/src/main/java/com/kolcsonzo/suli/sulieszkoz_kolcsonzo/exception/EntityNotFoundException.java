package com.kolcsonzo.suli.sulieszkoz_kolcsonzo.exception;

// 404 - amikor egy entitas nem talalhato ID vagy mas mezo alapjan
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}