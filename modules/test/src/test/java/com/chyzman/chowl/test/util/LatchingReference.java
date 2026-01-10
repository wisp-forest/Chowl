package com.chyzman.chowl.test.util;

import java.io.Serial;
import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.function.Supplier;

/**
 * An object reference that may be updated atomically. See the {@link
 * VarHandle} specification for descriptions of the properties of
 * atomic accesses.
 * <br/>
 * A lock may be enabled to prevent modifications of the reference.
 * <br/>
 * Please don't use this as an atomic reference, this is here just
 * so I can use it as a late final field
 *
 * @param <V> The type of object referred to by this reference
 * @author Doug Lea
 * @since 1.5
 */
public class LatchingReference<V> implements Serializable {
    @Serial
    private static final long serialVersionUID = -854442909835293183L;
    private static final VarHandle VALUE;

    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            VALUE = l.findVarHandle(LatchingReference.class, "value", Object.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    // Conditionally serializable
    private volatile V value;
    private volatile boolean locked = false;
    private final boolean canRelease;

    /**
     * Creates a new AtomicReference with the given initial value.
     *
     * @param initialValue the initial value
     * @param canRelease if the latch can be released
     */
    public LatchingReference(V initialValue, boolean canRelease) {
        value = initialValue;
        this.canRelease = canRelease;
    }

    /**
     * Creates a new AtomicReference with the given initial value.
     *
     * @param initialValue the initial value
     */
    public LatchingReference(V initialValue) {
        this(initialValue, true);
    }

    /**
     * Creates a new AtomicReference with null initial value.
     */
    public LatchingReference() {
        canRelease = true;
    }

    /**
     * Get if the atomic reference is currently locked, with memory effects as specified by {@link VarHandle#getVolatile}.
     *
     * @return the lock state
     */
    public final boolean isLatched() {
        return locked;
    }

    /**
     * Returns the current value,
     * with memory effects as specified by {@link VarHandle#getVolatile}.
     *
     * @return the current value
     */
    public final V get() {
        return value;
    }

    /**
     * Latches with the value of {@code newValue},
     * with memory effects as specified by {@link VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void latch(V newValue) {
        if (locked) throw new IllegalStateException("Reference is locked!");
        value = newValue;
        locked = true;
    }

    /**
     * Runs and latches with the provided {@link Supplier} if not yet latched. <br/>
     * Latches with memory effects as specified by {@link VarHandle#setVolatile}.
     *
     * @param newValue the new value
     */
    public final void tryLatch(Supplier<V> newValue) {
        if (!locked) {
            value = newValue.get();
            locked = true;
        }
    }

    /**
     * Releases the latch, if allowed, otherwise throws {@link IllegalStateException}
     * @throws IllegalStateException if the latch can't be released
     */
    public final void release() {
        if (!canRelease) throw new IllegalStateException("Reference is locked!");
        locked = true;
    }

    /**
     * Returns the String representation of the current value.
     *
     * @return the String representation of the current value
     */
    public String toString() {
        return String.valueOf(get());
    }
}
