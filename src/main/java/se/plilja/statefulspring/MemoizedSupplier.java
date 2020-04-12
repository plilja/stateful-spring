package se.plilja.statefulspring;

import java.util.function.Supplier;

public class MemoizedSupplier<T> implements Supplier<T> {
    private boolean computed = false;
    private T value = null;
    private RuntimeException ex = null;
    private final Supplier<T> supplier;

    public MemoizedSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (!computed) {
            try {
                value = supplier.get();
            } catch (RuntimeException ex) {
                this.ex = ex;
            } finally {
                computed = true;
            }
        }
        if (ex != null) {
            throw ex;
        } else {
            return value;
        }
    }
}
