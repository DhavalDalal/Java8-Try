package com.tsys.utils;

@FunctionalInterface
public interface SupplierThrowsException<T, E extends Throwable> {
    T get() throws E;
}
