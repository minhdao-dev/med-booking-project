package com.medbooking.common.exception;

public final class ResourceNotFoundException extends AppException {

    public ResourceNotFoundException(String resource, Object identifier) {
        super(404, "%s không tìm thấy với giá trị: %s".formatted(resource, identifier));
    }
}