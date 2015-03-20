package com.tsys.utils;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Failure<T extends Throwable> implements Try<T> {
    private final T throwable;

    public Failure(final T throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean isSuccess() { return false; }

    @Override
    public boolean isFailure() { return true;  }

    @Override
    public T get() { throw new RuntimeException(throwable); }

    @Override
    public void forEach(Consumer<? super T> fn) { }

    @Override
    public Try<T> filter(Predicate<? super T> predicate) {
        return this;
    }

    @Override
    public <R> Try<R> recover(Function<? super T, R> fn) {
        try {
            return new Success<>(fn.apply(throwable));
        } catch (Throwable t) {
            return new Failure(t);
        }
    }

    @Override
    public<R> Try<R> map(Function<? super T, ? extends R> mapper) {
        return (Try<R>) this;
    }

    @Override
    public <R> Try<R> flatMap(Function<? super T, Try<R>> fn) {
        return (Try<R>) this;
    }

    @Override
    public String toString() {
        return String.format("Failure(%s)", throwable.toString());
    }
}
