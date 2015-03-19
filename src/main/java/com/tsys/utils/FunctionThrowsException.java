package util;

/**
 * Created by dhavald on 19/03/15.
 */
@FunctionalInterface
interface FunctionThrowsException<T, R, E extends Throwable> {
    public R apply(T t) throws E;
}
