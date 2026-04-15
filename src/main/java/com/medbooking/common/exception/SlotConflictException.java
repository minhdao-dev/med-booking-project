package com.medbooking.common.exception;

public final class SlotConflictException extends AppException {

    public SlotConflictException() {
        super(409, "Slot vừa được đặt bởi người khác. Vui lòng chọn slot khác.");
    }
}