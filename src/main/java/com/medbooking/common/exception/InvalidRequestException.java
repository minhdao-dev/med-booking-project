package com.medbooking.common.exception;

public final class InvalidRequestException extends AppException {

    public InvalidRequestException(String message) {
        super(400, message);
    }
}