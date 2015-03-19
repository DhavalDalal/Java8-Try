package com.tsys.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TryInCollectionExamples {
    public static void main(String[] args) throws Exception {
        //Example: FunctionThrowingException, map
        List<Try<String>> mapped = Arrays.asList("Hello", null, "dance").stream()
                .map(s -> Try.with((FunctionThrowsException<String, String, Exception>) TrySpecsUtil::capitalize, s))
                .collect(Collectors.toList());
        System.out.println("mapped = " + mapped);

        //Example: FunctionThrowingException encapsulated in Try, map
        List<Try<String>> mapEncapsulated = Arrays.asList("Hello", null, "dance").stream()
                .map(s -> TrySpecsUtil.prefixCapitalize("--> ", s))
                .collect(Collectors.toList());
        System.out.println("mapEncapsulated = " + mapEncapsulated);

        //Example: ConsumerThrowingException
        Arrays.asList("Hello", null, "dance").stream()
                .forEach(s -> Try.with(TrySpecsUtil::printCapitalized, s));

        //Example: SupplierThrowingException
        final List<Try<String>> strings = Stream.generate(() -> Try.with(() -> TrySpecsUtil.generate()))
                .filter(t -> t instanceof Success)
                .limit(3)
                .collect(Collectors.toList());

        System.out.println("strings = " + strings);


        //Example: PredicateThrowingException
        final List<String> filtered = Arrays.asList("Hello", null, "hi").stream()
                .filter(s -> Try.with(TrySpecsUtil::gte5).test(s))
                .collect(Collectors.toList());
        System.out.println("filtered = " + filtered);

    }
}
