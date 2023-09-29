package com.chyzman.chowl.transfer;

public class TransferState {
    public static final ThreadLocal<Boolean> TRAVERSING = ThreadLocal.withInitial(() -> false);
}
