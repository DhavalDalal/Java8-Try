package com.tsys.utils;

@FunctionalInterface
interface SupplierThrowsException<T, E extends Throwable> {
    T get() throws E;
}
