package com.tsys.utils;

@FunctionalInterface
public interface BiConsumerThrowsException<T, U, E extends Throwable> {
    void accept(T t, U u) throws E;
}
