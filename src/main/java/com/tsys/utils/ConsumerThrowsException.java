package com.tsys.utils;

@FunctionalInterface
public interface ConsumerThrowsException<T, E extends Throwable> {
    void accept(T t) throws E;
}
