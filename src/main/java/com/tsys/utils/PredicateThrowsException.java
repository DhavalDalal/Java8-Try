package com.tsys.utils;

@FunctionalInterface
public interface PredicateThrowsException<T, E extends Throwable> {
    boolean test(T t) throws E;
}
