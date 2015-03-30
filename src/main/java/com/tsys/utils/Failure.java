package com.tsys.utils;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Failure<T> extends Try<T> {
    private final Throwable throwable;

    public Failure(final Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public boolean isSuccess() { return false; }

    @Override
    public boolean isFailure() { return true;  }

    @Override
    public T get() {
        throw new RuntimeException(throwable);
    }

    @Override
    public void forEach(Consumer<? super T> fn) { }

    @Override
    public Try<T> filter(Predicate<? super T> predicate) {
        return this;
    }

    @Override
    public <R> Try<R> recover(Function<Throwable, R> fn) {
        Objects.requireNonNull(fn);
        try {
            return new Success<>(fn.apply(throwable));
        } catch (Throwable t) {
            return rethrowIfFatal(t);
        }
    }

    @Override
    public<R> Try<R> recoverWith(Function<Throwable, Try<R>> fn) {
        Objects.requireNonNull(fn);
        try {
            return fn.apply(throwable);
        } catch (Throwable t) {
            return rethrowIfFatal(t);
        }
    }

    @Override
    public<R> Try<R> transform(Function<T, Try<R>> s, Function<Throwable, Try<R>> fn) {
        try {
            return fn.apply(throwable);
        } catch (Throwable t) {
            return rethrowIfFatal(t);
        }
    }

    @Override
    public Try<T> failed() {
        return new Success(throwable);
    }

    @Override
    public <R extends Try<?>> R flatten() {
        return (R) this;
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
