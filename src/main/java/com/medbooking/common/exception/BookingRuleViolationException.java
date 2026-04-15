package com.medbooking.common.exception;

public final class BookingRuleViolationException extends AppException {

    public BookingRuleViolationException(String message) {
        super(422, message);
    }
}