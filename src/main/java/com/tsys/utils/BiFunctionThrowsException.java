package com.tsys.utils;

@FunctionalInterface
public interface BiFunctionThrowsException<T, U, R, E extends Throwable> {
    R apply(T t, U u) throws E;
}
