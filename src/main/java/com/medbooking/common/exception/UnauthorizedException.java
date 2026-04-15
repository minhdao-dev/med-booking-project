package com.medbooking.common.exception;

public final class UnauthorizedException extends AppException {

    public UnauthorizedException(String message) {
        super(401, message);
    }
}