package com.tsys.utils;

public class RecoveryExamples {
    public static void main(String[] args) {
        final Try<Integer> answer = Try.with(() -> 2 / 0)
                .recover(t -> 2)
                .map(x -> {
                    throw new RuntimeException();
                })
                .recover(t -> 4);
        System.out.println("answer = " + answer);

        final Try<Integer> answer2 = Try.with(() -> 12 / 0)
                .recover(t -> 2)
                .map(x -> x * 4)
                .recover(t -> 4);
        System.out.println("answer2 = " + answer2);

        final Try<Double> answer3 = Try.with(() -> 2)
                .recover(t -> 2)
                .map(x -> {
                    throw new RuntimeException("fatal");
                })
                .recoverWith(t -> Try.with(() -> 10d));
        System.out.println("answer3 = " + answer3);
    }
}
