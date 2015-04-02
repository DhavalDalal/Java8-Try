package examples;

import com.tsys.utils.*;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TryInCollectionExamples {

    private static final Random random = new Random();

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


    static void printCapitalized(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        System.out.println(s.toUpperCase());

    }

    static String generate() throws Exception {
        if(random.nextInt(3) == 0)
            throw new Exception("Could Not Generate String");

        final StringBuilder name = random.ints(97, 122)
                .limit(random.nextInt(10))
                .mapToObj(c -> Character.valueOf((char) c))
                .filter(Character::isAlphabetic)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append);

        return name.toString();
    }

    static boolean gte5(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        return s.length() >= 5;
    }


    public static void main(String[] args) throws Exception {
        //Example: FunctionThrowingException, map
        List<Try<String>> mapped = Arrays.asList("Hello", null, "dance").stream()
                .map(s -> Try.with((FunctionThrowsException<String, String, Exception>) TryInCollectionExamples::capitalize, s))
                .collect(Collectors.toList());
        System.out.println("mapped = " + mapped);

        //Example: FunctionThrowingException encapsulated in Try, map
        List<Try<String>> mapEncapsulated = Arrays.asList("Hello", null, "dance").stream()
                .map(s -> TryInCollectionExamples.prefixCapitalize("--> ", s))
                .collect(Collectors.toList());
        System.out.println("mapEncapsulated = " + mapEncapsulated);

        //Example: ConsumerThrowingException
        ConsumerThrowsException<String, Exception> printCapitalized = TryInCollectionExamples::printCapitalized;
        Arrays.asList("Hello", null, "dance").stream()
                .forEach(s -> Try.with(printCapitalized, s));

        //Example: SupplierThrowingException
        SupplierThrowsException<String, Exception> generator = () -> TryInCollectionExamples.generate();
        final List<Try<String>> strings = Stream.generate(() -> Try.with(generator))
                .filter(t -> t instanceof Success)
                .limit(3)
                .collect(Collectors.toList());

        System.out.println("strings = " + strings);


        //Example: PredicateThrowingException
        final List<String> filtered = Arrays.asList("Hello", null, "hi").stream()
                .filter(Try.with(TryInCollectionExamples::gte5))
                .collect(Collectors.toList());
        System.out.println("filtered = " + filtered);

    }
}


