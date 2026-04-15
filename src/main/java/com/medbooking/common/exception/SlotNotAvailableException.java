package com.medbooking.common.exception;

public final class SlotNotAvailableException extends AppException {

    public SlotNotAvailableException(String message) {
        super(400, message);
    }
}