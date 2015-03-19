package util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Failure<T extends Throwable> implements Try<T> {
    private final T throwable;

    public Failure(final T throwable) {
        this.throwable = throwable;
    }

    public boolean isSuccess() { return false; }
    public boolean isFailure() { return true;  }
    public T get() { throw new RuntimeException(throwable); }
    public void forEach(Consumer<T> fn) { }
    public Try<T> filter(Predicate<T> predicate) {
        return this;
    }
    public<R> Try<R> map(Function<T, R> mapper) {
        return (Try<R>) this;
    }
    public <R> Try<R> flatMap(Function<T, Try<R>> fn) {
        return (Try<R>) this;
    }

    public String toString() {
        return String.format("Failure(%s)", throwable.toString());
    }
}
