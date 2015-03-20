package com.tsys.utils;

/**
 * Created by dhavald on 21/03/15.
 */
class TrySpecsUtil {

    static String methodAlwaysThrows() throws Exception {
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

        return Try.with(() -> prefix + capitalize(s));
    }

    static int length(String s) {
        if (null == s)
            throw new IllegalArgumentException("null string");

        return s.length();
    }


    static boolean gte5(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        return s.length() < 5 ? false : true;
    }

}
