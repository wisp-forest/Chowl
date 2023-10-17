package com.chyzman.chowl.transfer;

public class TransferState {
    public static final ThreadLocal<Boolean> TRAVERSING = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> DOUBLE_CLICK_INSERT = ThreadLocal.withInitial(() -> false);
    public static final ThreadLocal<Boolean> NO_BLANK_DRAWERS = ThreadLocal.withInitial(() -> false);
}
