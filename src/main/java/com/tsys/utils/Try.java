package util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Try<T> {
    public boolean isSuccess();
    public boolean isFailure();
    public T get();
    public<R> Try<R> map(Function<T, R> fn);
    public<R> Try<R> flatMap(Function<T, Try<R>> fn);
    public void forEach(Consumer<T> fn);
    public Try<T> filter(Predicate<T> predicate);

    default Optional<T> toOptional() {
        if (isSuccess()) {
            return Optional.of(get());
        } else {
            return Optional.empty();
        }
    }

    static<T, R, E extends Throwable> Try<R> with(FunctionThrowsException<T, R, E> fte, T t) {
        Objects.requireNonNull(fte);
        try { return new Success<>(fte.apply(t)); }
        catch(Throwable e) { return new Failure(e); }
    }

    static<T, E extends Throwable> Try<T> with(SupplierThrowsException<T, E> ste) {
        Objects.requireNonNull(ste);
        try { return new Success<>(ste.get()); }
        catch(Throwable e) { return new Failure(e); }
    }

    static<T, E extends Throwable> Try<T> with(PredicateThrowsException<T, E> pte, T t) {
        Objects.requireNonNull(pte);
        try {
            return pte.test(t) ? new Success<>(t)
                    : new Failure(new NoSuchElementException("predicate does not hold"));
        } catch (Throwable e) {
            return new Failure(e);
        }
    }

    static<T, E extends Throwable> Predicate<T> with(PredicateThrowsException<T, E> pte) {
        Objects.requireNonNull(pte);
        return t -> {
            try {
                return pte.test(t) ? true : false;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    static<T, E extends Throwable> Try<Void> with(ConsumerThrowsException<T, E> cte, T t) {
        Objects.requireNonNull(cte);
        try {
            cte.accept(t);
            return new Success<>(null);
        } catch (Throwable e) {
            return new Failure(e);
        }
    }
}




