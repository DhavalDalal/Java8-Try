package com.tsys.utils;

@FunctionalInterface
interface FunctionThrowsException<T, R, E extends Throwable> {
    public R apply(T t) throws E;
}
