package com.tsys.utils;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.*;

/**
 *
 * The `Try` type represents a computation that may either result in an exception,
 * or return a successfully computed value.
 *
 * Instances of `Try<T>`, are either an instance of Success<T> or Failure<T>.
 *
 * For example, `Try` can be used to perform division on a user-defined input, without
 * the need to do explicit exception-handling in all of the places that an exception might occur.
 *
 * Example:
 * {{{
 *
 * static String readKeyboard(String prompt) {
 *   Scanner in = new Scanner(System.in);
 *   System.out.print(prompt);
 *   return in.nextLine();
 * }
 *
 * static Try<Integer> divide() {
 *   String numer = readKeyboard("Enter an Int that you'd like to divide:");
 *   String denom = readKeyboard("Enter an Int that you'd like to divide by:");
 *
 *   FunctionThrowsException<String, Integer, NumberFormatException> toInteger = s -> Integer.parseInt(s);
 *   Try<Integer> dividend = Try.with(toInteger, numer);
 *   Try<Integer> divisor = Try.with(toInteger, denom);
 *   return dividend.flatMap(x -> divisor.map(y -> x / y))
 *                  .recoverWith(t -> divide());
 * }
 *
 * divide().forEach(System.out::println);
 *
 * }}}
 *
 * An important property of `Try` shown in the above example is its ability to ''pipeline'', or chain, operations,
 * catching exceptions along the way. The `flatMap` and `map` combinators in the above example each essentially
 * pass off either their successfully completed value, wrapped in the `Success` type for it to be further operated
 * upon by the next combinator in the chain, or the exception wrapped in the `Failure` type usually to be simply
 * passed on down the chain. Combinators such as `recover` is designed to provide some type of default behavior
 * in the case of failure.
 *
 * Note: only non-fatal exceptions are caught by the combinators on `Try`. Serious system errors, on the other hand,
 * will be thrown.
 *
 * Note: All Try combinators will catch exceptions and return failure unless otherwise specified in the documentation.
 *
 * `Try` here is an attempt to translate the Scala standard library Try[T], which was based on Scala Try's original
 * implementation at Twitter.
 */

public abstract class Try<T> {

    /**
     * Returns `true` if the `Try` is a `Failure`, `false` otherwise.
     */
    public abstract boolean isSuccess();

    /**
     * Returns `true` if the `Try` is a `Success`, `false` otherwise.
     */
    public abstract  boolean isFailure();

    /** 
     * Returns the value from this `Success` or throws the exception if this is a `Failure`.
     */
    public abstract T get();

    /**
     * Returns the value from this `Success` or the given `default` argument if this is a `Failure`.
     *
     * ''Note:'': This will throw an exception if it is not a success and default throws an exception.
     */
    public T getOrElse(final T defaultValue) {
        return isSuccess() ? get() : defaultValue;
    }

    /**
     * Returns this `Try` if it's a `Success` or the given `default` argument if this is a `Failure`.
     */
    public Try<T> orElse(final Try<T> defaultValue) {
        try {
            return isSuccess() ? this : defaultValue;
        } catch(Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Returns the given function applied to the value from this `Success`
     * or returns this if this is a `Failure`.
     */
    public abstract<R> Try<R> map(Function<? super T, ? extends R> fn);

    /**
     * Returns the given function applied to the value from this `Success` or returns this if this is a `Failure`.
     */
    public abstract<R> Try<R> flatMap(Function<? super T, Try<R>> fn);

    /**
     * Applies the given function `fn` if this is a `Success`, otherwise returns
     * `Unit` if this is a `Failure`.
     *
     * ''Note:'' If `fn` throws, then this method may throw an exception.
     */
    public abstract void forEach(Consumer<? super T> fn);

    /**
     * Converts this to a `Failure` if the predicate is not satisfied.
     */
    public abstract Try<T> filter(Predicate<? super T> predicate);

    /**
     * Applies the given function `fn` if this is a `Failure`, otherwise returns this if this is a `Success`.
     * This is like map for the exception.
     */
    public abstract<R> Try<R> recover(Function<Throwable, R> fn);

    /**
     * Applies the given function `fn` if this is a `Failure`, otherwise returns this if this is a `Success`.
     * This is like `flatMap` for the exception.
     */
    public abstract<R> Try<R> recoverWith(Function<Throwable, Try<R>> fn);

    /**
     * Completes this `Try` by applying the function `fn` to this if this
     * is of type `Failure`, or conversely, by applying `s` if this is a `Success`.
     */
    public abstract<R> Try<R> transform(Function<T, Try<R>> s, Function<Throwable, Try<R>> fn);

    /**
     * Completes this `Try` with an exception wrapped in a `Success`. The
     * exception is either the exception that the `Try` failed with (if a `Failure`)
     * or an `UnsupportedOperationException`.
     */
    public abstract Try<T> failed();

    /**
     * Returns `empty` if this is a `Failure` or a `Optional` containing the
     * value if this is a `Success`.
     */
    public Optional<T> toOptional() {
        return isSuccess() ? Optional.of(get()) : Optional.empty();
    }

    /**
     * Transforms a nested `Try`, ie, a `Try` of type `Try<Try<T>>`,
     * into an un-nested `Try`, ie, a `Try` of type `Try<T>`.
     */
    public abstract <R extends Try<?>> R flatten();

    /**
     * Constructs a `Try` using a supplier that throws checked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T, E extends Throwable> Try<T> with(SupplierThrowsException<T, E> ste) {
        Objects.requireNonNull(ste);
        try {
            return new Success<>(ste.get());
        } catch (Throwable t) {
            return rethrowIfFatal(t);
        }
    }

    /**
     * Constructs a `Try` using a supplier that throws unchecked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T> Try<T> with(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        SupplierThrowsException<T, Throwable> ste = () -> supplier.get();
        return Try.with(ste);
    }


    /**
     * Constructs a `Try` using a function that throws checked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T, R, E extends Throwable> Try<R> with(FunctionThrowsException<T, R, E> fte, T t) {
        Objects.requireNonNull(fte);
        SupplierThrowsException<R, E> ste = () -> fte.apply(t);
        return Try.with(ste);
    }

    /**
     * Constructs a `Try` using a function that throws unchecked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T, R> Try<R> with(Function<T, R> fn, T t) {
        Objects.requireNonNull(fn);
        return Try.with((Supplier<R>) () -> fn.apply(t));
    }

    /**
     * Constructs a `Try` using a predicate that throws checked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T, E extends Throwable> Try<T> with(PredicateThrowsException<T, E> pte, T t) {
        Objects.requireNonNull(pte);
        try {
            return pte.test(t) ? new Success<>(t)
                    : new Failure(new NoSuchElementException("predicate does not hold"));
        } catch (Throwable e) {
            return rethrowIfFatal(e);
        }
    }

    /**
     * Constructs a `Try` using a predicate that throws unchecked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T> Try<T> with(Predicate<T> predicate, T t) {
        Objects.requireNonNull(predicate);
        try {
            return predicate.test(t) ? new Success<>(t)
                    : new Failure<>(new NoSuchElementException("predicate does not hold"));
        } catch (Throwable e) {
            return rethrowIfFatal(e);
        }
    }

    /**
     * Constructs a `Try` using a consumer that throws checked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T, E extends Throwable> Try<T> with(ConsumerThrowsException<T, E> cte, T t) {
        Objects.requireNonNull(cte);
        SupplierThrowsException<T, E> ste = () -> {
            cte.accept(t);
            return t;
        };
        return Try.with(ste);
    }

    /**
     * Constructs a `Try` using a consumer that throws unchecked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T> Try<T> with(Consumer<T> consumer, T t) {
        Objects.requireNonNull(consumer);
        Supplier<T> supplier = () -> {
            consumer.accept(t);
            return t;
        };
        return Try.with(supplier);
    }

    /**
     * Constructs a `Try` using a Bi-function that throws checked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T, U, R, E extends Throwable> Try<R> with(BiFunctionThrowsException<T, U, R, E> bfte, T t, U u) {
        Objects.requireNonNull(bfte);
        SupplierThrowsException<R, E> ste = () -> bfte.apply(t, u);
        return Try.with(ste);
    }

    /**
     * Constructs a `Try` using a Bi-function that throws unchecked exception.
     * This method will ensure any non-fatal exception is caught and a `Failure` object
     * is returned.
     */
    public static<T, U, R> Try<R> with(BiFunction<T, U, R> biFn, T t, U u) {
        Objects.requireNonNull(biFn);
        return Try.with((Supplier<R>) () -> biFn.apply(t, u));
    }

    /**
     * Wrap a predicate that throws exception in a predicate that can take care of
     * the checked exception and morph it to a Predicate.  Typically useful in
     * cases where collections are to be filtered and returning a Try<T> in
     * such a scenario is not type-correct.
     */
    public static<T, E extends Throwable> Predicate<T> with(PredicateThrowsException<T, E> pte) {
        Objects.requireNonNull(pte);
        return t -> {
            try {
                return pte.test(t) ? true : false;
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    static<T> Try<T> rethrowIfFatal(final Throwable t) {
        if (fatal(t)) {
            throw new RuntimeException(t);
        }
        return new Failure<>(t);
    }

    static boolean fatal(final Throwable t) {
        Class<? extends Throwable> throwableClass = t.getClass();

        // StackOverflowError ok even though it is a VirtualMachineError
        if(throwableClass == StackOverflowError.class) {
            return false;
        }

        if (t instanceof VirtualMachineError
            || throwableClass == ThreadDeath.class
            || throwableClass == InterruptedException.class
            || throwableClass == LinkageError.class) {
            return true;
        }

       return false;
    }
}
