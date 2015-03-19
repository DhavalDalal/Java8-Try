package util;

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
    public boolean isSuccess() { return true;  }
    public boolean isFailure() { return false; }
    public T get() { return value; }
    public void forEach(Consumer<T> fn) { fn.accept(value); }

    public Try<T> filter(Predicate<T> predicate) {
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

    public <R> Try<R> map(Function<T, R> mapper) {
        Objects.requireNonNull(mapper);
        try { return new Success<>(mapper.apply(value)); }
        catch (Throwable t) { return new Failure(t); }
    }

    public <R> Try<R> flatMap(Function<T, Try<R>> mapper) {
        Objects.requireNonNull(mapper);
        try { return mapper.apply(value); }
        catch(Throwable t) { return new Failure(t); }
    }

    public String toString() {
        return String.format("Success(%s)", value);
    }
}
