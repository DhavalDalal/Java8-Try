package util;

/**
 * Created by dhavald on 19/03/15.
 */
@FunctionalInterface
interface ConsumerThrowsException<T, E extends Throwable> {
    void accept(T t) throws E;
}
