package com.medbooking.common.exception;

import lombok.Getter;

@Getter
public sealed class AppException extends RuntimeException
        permits ResourceNotFoundException,
        DuplicateResourceException,
        InvalidRequestException,
        UnauthorizedException,
        ForbiddenException,
        SlotNotAvailableException,
        SlotConflictException,
        BookingRuleViolationException {

    private final int status;

    protected AppException(int status, String message) {
        super(message);
        this.status = status;
    }
}