package com.tsys.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Success<T> implements Try<T> {
    private final T value;

    public Success(final T value) {
        this.value = value;
    }

    @Override
    public boolean isSuccess() { return true;  }

    @Override
    public boolean isFailure() { return false; }

    @Override
    public T get() { return value; }

    @Override
    public void forEach(Consumer<? super T> fn) {
        Objects.requireNonNull(fn);
        fn.accept(value);
    }

    @Override
    public Try<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        try {
            if (predicate.test(value))
                return this;
            else
                return new Failure(new NoSuchElementException("predicate does not hold"));
        } catch (Throwable t) {
            return new Failure(t);
        }
    }

    @Override
    public<R> Try<R> recover(Function<Throwable, R> fn) {
        return (Try<R>) this;
    }

    @Override
    public<R> Try<R> recoverWith(Function<Throwable, Try<R>> fn) {
        return (Try<R>) this;
    }

    @Override
    public <R> Try<R> transform(Function<T, Try<R>> successFn, Function<Throwable, Try<R>> failureFn) {
        try {
            return successFn.apply(value);
        } catch (Throwable t) {
            return new Failure(t);
        }
    }

    @Override
    public Try<T> failed() {
        return new Failure(new UnsupportedOperationException("Success failed"));
    }

    @Override
    public <R> Try<R> map(Function<? super T, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        try { return new Success<>(mapper.apply(value)); }
        catch (Throwable t) { return new Failure(t); }
    }

    @Override
    public <R> Try<R> flatMap(Function<? super T, Try<R>> mapper) {
        Objects.requireNonNull(mapper);
        try { return mapper.apply(value); }
        catch(Throwable t) { return new Failure(t); }
    }

    @Override
    public String toString() {
        return String.format("Success(%s)", value);
    }
}
