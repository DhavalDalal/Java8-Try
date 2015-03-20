package com.tsys.utils;

@FunctionalInterface
interface ConsumerThrowsException<T, E extends Throwable> {
    void accept(T t) throws E;
}
