package com.tsys.utils;

/**
 * Created by dhavald on 19/03/15.
 */
@FunctionalInterface
interface PredicateThrowsException<T, E extends Throwable> {
    boolean test(T t) throws E;
}
