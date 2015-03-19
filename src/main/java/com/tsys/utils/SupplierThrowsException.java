package util;

/**
 * Created by dhavald on 19/03/15.
 */
@FunctionalInterface
interface SupplierThrowsException<T, E extends Throwable> {
    T get() throws E;
}
