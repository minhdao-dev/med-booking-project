package com.medbooking.common.exception;

public final class ForbiddenException extends AppException {

    public ForbiddenException(String message) {
        super(403, message);
    }
}