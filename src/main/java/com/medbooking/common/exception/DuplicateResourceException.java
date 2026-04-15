package com.medbooking.common.exception;

public final class DuplicateResourceException extends AppException {

    public DuplicateResourceException(String resource, String field, Object value) {
        super(409, "%s đã tồn tại với %s: %s".formatted(resource, field, value));
    }
}