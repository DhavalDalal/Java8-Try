package com.tsys.utils;

@FunctionalInterface
interface PredicateThrowsException<T, E extends Throwable> {
    boolean test(T t) throws E;
}
