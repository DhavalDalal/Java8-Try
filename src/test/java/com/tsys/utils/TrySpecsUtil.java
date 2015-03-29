package com.tsys.utils;

public class TrySpecsUtil {

    public static String methodAlwaysThrows() throws Exception {
        throw new Exception("failure");
    }

    static String capitalize(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        return s.toUpperCase();
    }

    static Try<String> prefixCapitalize(String prefix, String s) {
        if(null == prefix)
            throw new IllegalArgumentException("null prefix");

        return Try.with((SupplierThrowsException<String, Exception>) () -> prefix + capitalize(s));
    }

    public static boolean gte5(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        return s.length() < 5 ? false : true;
    }

}
