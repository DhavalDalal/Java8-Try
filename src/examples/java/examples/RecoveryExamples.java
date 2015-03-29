package examples;

import com.tsys.utils.Try;

import java.util.function.Supplier;

public class RecoveryExamples {

    private static Supplier<Integer> divisionByZero = () -> 2 / 0;

    private static Supplier<Integer> value = () -> 2;

    private static Supplier<Double> nextValue = () -> 10d;

    public static void main(String[] args) {

        final Try<Integer> answer = Try.with(divisionByZero)
                .recover(t -> 2)
                .map(x -> {
                    throw new RuntimeException();
                })
                .recover(t -> 4);
        System.out.println("answer = " + answer);

        final Try<Integer> answer2 = Try.with(divisionByZero)
                .recover(t -> 2)
                .map(x -> x * 4)
                .recover(t -> 4);
        System.out.println("answer2 = " + answer2);

        final Try<Double> answer3 = Try.with(value)
                .recover(t -> 2)
                .map(x -> {
                    throw new RuntimeException("fatal");
                })
                .recoverWith(t -> Try.with(nextValue));
        System.out.println("answer3 = " + answer3);

        //Chain of Responsibility using recover/recoverWith
        final Try<Double> answer4 =
//                Try.with(divisionByZero) //Success(10.0)
                Try.with((Supplier<Integer>) () -> Integer.valueOf(null)) //Success(4)
                .recover(t -> {
                    if (t.getClass() == NumberFormatException.class) {
                        return 2;
                    }
                    throw new RuntimeException(t);
                })
                .map(x -> x * 2)
                .recoverWith(t -> Try.with(nextValue));
        System.out.println("answer4 = " + answer4);

    }
}
