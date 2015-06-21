package examples;

import com.tsys.utils.BiConsumerThrowsException;
import com.tsys.utils.BiFunctionThrowsException;
import com.tsys.utils.ConsumerThrowsException;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParallelStreams {
    
    static boolean isPrime(int n) {
        return Stream.iterate(2, x -> x + 1).limit(n - 2).allMatch(x -> n % x != 0);
    }

    static<T> T twice(Function<T, T> fn, T t) {
        return fn.apply(fn.apply(t));
    }

    static void iterate(int howMany, Runnable runnable) {
        if (howMany <= 0) {
            return;
        }
        runnable.run();
        iterate(howMany - 1, runnable);
    }

    static<T> T iterate(Integer howMany, Function<T, T> fn, T t) {
        if (howMany <= 0) return t;
        else return iterate(howMany - 1, fn, fn.apply(t));
    }

    static Double expensiveSquare(Double number) {
        try {
            Thread.sleep(2 * 1000);
            return number * number;
        } catch (InterruptedException ie) {
            return Double.NaN;
        }
    }

    static Stream<Integer> naturals(int from) {
        assert from >= 0;

        return Stream.iterate(from, x -> x + 1);
    }

    static Stream<Integer> naturals(int from, int until) {
        assert from >= 0;
        return Stream.iterate(from, x -> x + 1).limit(until - 1);
    }

    static List<List<?>> combinations(List<?> one, List<?> two) {
        return one.stream()
                .flatMap(f -> two.stream().map(s ->
                        Arrays.asList(f, s)))
                .collect(Collectors.toList());
    }

    static List<Double> timer(Stream<Double> numbers) {
        long startTime = System.currentTimeMillis();
        List<Double> squares = numbers
                .map(ParallelStreams::expensiveSquare)
                .collect(Collectors.toList());
        long timeTaken = System.currentTimeMillis() - startTime;
        System.out.println(String.format("Time Taken: %s ms, squares: %s", timeTaken, squares));
        return squares;
    }

    static<T, R> R timer(Function<T, R> fn, T t) {
        long startTime = System.currentTimeMillis();
        R result = fn.apply(t);
        long timeTaken = System.currentTimeMillis() - startTime;
        System.out.println("Time: " + timeTaken + " ms");
        return result;
    }

    static Function<Double, Double> power(double raiseTo) {
        return x -> Math.pow(x, raiseTo);
    }



    public static void main(String[] args) {
        final List<Double> numbers = Arrays.asList(1.0, 2.0, 3.0, 4.0, 5.0);
//        ParallelStreams.timer(numbers.stream());
//        ParallelStreams.timer(numbers.parallelStream());

//        Double result = ParallelStreams.timer(ParallelStreams::expensiveSquare, 10.0);
//        System.out.println("result = " + result);

//        final int value = 1;
//        final Integer once = iterate(2, Function.identity(), value);
//        System.out.println("once = " + once);
//
//        final Integer doubled = iterate(1, x -> x + x, value);
//        System.out.println("doubled = " + doubled);


//        try (Resource resource = new Resource("SQL Connection")) {
//            resource.write("something");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        Resource.use("SQL Connection", r -> r.write("something"));
        Resource.use2("SQL Connection", "file", (c, f) -> {
           c.write("something");
           f.write("more");
           throw new Exception("on purpose");
        });

        iterate(2, () -> System.out.println("Hello"));

        Function<Integer, Function<Integer, Integer>> add =
                x -> y -> x + y;

        final Integer result = add.apply(3).apply(5);
        System.out.println("result = " + result);

        Function<Integer, Integer> incr = add.apply(1);
        Function<Integer, Integer> decr = add.apply(-1);

        System.out.println("incr.apply(2) = " + incr.apply(2));
        System.out.println("decr.apply(2) = " + decr.apply(2));

        Function<Double, Double> square = power(2.0);
        System.out.println("square.apply(2.0) = " + square.apply(2.0));

        Function<Double, Double> cube = power(3.0);
        System.out.println("cube.apply(2.0) = " + cube.apply(2.0));

        System.out.println("isPrime(2) = " + isPrime(2));
        System.out.println("isPrime(3) = " + isPrime(3));
        System.out.println("isPrime(4) = " + isPrime(4));
        System.out.println("isPrime(5) = " + isPrime(5));

//        naturals(0).forEach(System.out::println);

        System.out.println("primePairs(4) = " + primePairs(5));
        System.out.println("primePairs(7) = " + primePairs(7));
        System.out.println("pythagorean(10) = " + pythagorean(10));
        System.out.println("pythagorean(5) = " + pythagorean(5));

        System.out.println("combinations = " + combinations(Arrays.asList('a', 'b'), Arrays.asList(1, 2)));
        System.out.println("combinations = " + combinations(Arrays.asList(1, 2), Arrays.asList(3, 4)));

    }

    private static List<List<Integer>> primePairs(int n) {
        return naturals(1, n).flatMap(i ->
                    naturals(1, i).map(j -> Arrays.asList(i, j)))
                .filter(pair -> isPrime(pair.get(0) + pair.get(1)))
                .collect(Collectors.toList());
    }

    private static List<List<Integer>> pythagorean(int n) {
        return naturals(1, n + 1).flatMap(x ->
                naturals(1, n + 1).flatMap(y ->
                        naturals(1, n + 1).map(z -> Arrays.asList(x, y, z))))
            .filter(tuple3 -> {
                int x = tuple3.get(0);
                int y = tuple3.get(1);
                int z = tuple3.get(2);
                return x * x + y * y == z * z;
            })
           .collect(Collectors.toList());
    }
}

class Resource implements AutoCloseable {
    private final String name;

    private Resource(String name) {
        this.name = name;
    }

    public void write(String data) throws Exception {
        System.out.println("Resource.write data = [" + data + "]");
    }

    @Override
    public void close() throws Exception {
        System.out.println("Resource.close [" + name + "]");
    }

    public static void use(String name, ConsumerThrowsException<Resource, Exception> consumer) {
        try (Resource resource = new Resource(name)) {
            consumer.accept(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void use2(String name1, String name2, BiConsumerThrowsException<Resource, Resource, Exception> consumer) {
        try (Resource resource1 = new Resource(name1)) {
            try(Resource resource2 = new Resource(name2))  {
                consumer.accept(resource1, resource2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
