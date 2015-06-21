package examples;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class Iteration {
    private static BiFunction<Integer, List<Integer>, Integer> sum0 = (acc, xs) -> {
        if(xs.isEmpty()) return acc;
        else return Iteration.sum0.apply(acc + xs.get(0), xs.subList(1, xs.size()));
    };

    public static Integer sum(List<Integer> ns) {
        return sum0.apply(0, ns);
    }


    public static void main(String[] args) {
        System.out.println("sum(Arrays.asList(1, 2, 3)) = " + sum(Arrays.asList(1, 2, 3)));
        System.out.println("sum(Arrays.asList()) = " + sum(Arrays.asList()));
    }
}
