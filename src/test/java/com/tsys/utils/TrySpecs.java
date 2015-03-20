package com.tsys.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TrySpecs {

    @Test
    public void successCreatedFromSupplierThrowingException() {
        //Given
        Integer value = 2;

        //When
        Try<Integer> success = Try.with(() -> value);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
    }

    @Test
    public void retrievesSuccessValueFromSupplierThrowingException() {
        //Given
        Integer value = 2;

        //When
        Try<Integer> success = Try.with(() -> value);

        //Then
        assertEquals(value, success.get());
    }

    @Test
    public void failureCreatedWhenSupplierThrowsException() {
        //Given-When
        Try<Integer> failure = Try.with(() -> 2 / 0);

        //Then
        assertTrue(failure.isFailure());
    }

    @Test
    public void shoutsWhenRetrievingValueFromFailureFromSupplierThrowingException() {
        //Given-When
        Try<Integer> failure = Try.with(() -> 2 / 0);

        //Then
        try {
            failure.get();
            fail("Cannot retrieve value from failure");
        } catch (RuntimeException rte) {
            assertTrue(rte.getCause() instanceof ArithmeticException);
        }
    }

    @Test
    public void successCreatedFromFunctionThrowingException() {
        //Given
        String value = "Hello";
        FunctionThrowsException<String, String, Exception> fte = s -> TrySpecsUtil.capitalize(s);

        //When
        Try<String> success = Try.with(fte, value);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
    }

    @Test
    public void retrievesSuccessValueFromFunctionThrowingException() {
        //Given
        String value = "Hello";
        FunctionThrowsException<String, String, Exception> fte = s -> TrySpecsUtil.capitalize(s);

        //When
        Try<String> success = Try.with(fte, value);

        //Then
        assertEquals(value.toUpperCase(), success.get());
    }

    @Test
    public void successCreatedFromConsumerThrowingException() {
        //Given
        String value = "Hello";
        ConsumerThrowsException<String, Exception> cte = s -> TrySpecsUtil.printCapitalized(s);

        //When
        Try<Void> success = Try.with(cte, value);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
    }

    @Test
    public void retrievesSuccessValueFromConsumerThrowingException() {
        //Given
        ConsumerThrowsException<String, Exception> cte = s -> TrySpecsUtil.printCapitalized(s);

        //When
        Try<Void> success = Try.with(cte, "Hello");

        //Then
        assertEquals(null, success.get());
    }

    @Test
    public void successCreatedFromPredicateThrowingException() {
        //Given
        final String value = "Hello";
        PredicateThrowsException<String, Exception> pte = s -> TrySpecsUtil.gte5(s);

        //When
        Try<String> success = Try.with(pte, value);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
    }

    @Test
    public void retrievesSuccessValueFromPredicateThrowingException() {
        //Given
        final String value = "Hello";
        PredicateThrowsException<String, Exception> pte = s -> TrySpecsUtil.gte5(s);

        //When
        Try<String> success = Try.with(pte, value);

        //Then
        assertEquals(value, success.get());
    }

    @Test
    public void convertsSuccessToString() {
        //Given
        PredicateThrowsException<String, Exception> pte = s -> TrySpecsUtil.gte5(s);

        //When
        Try<String> success = Try.with(pte, "Hello");

        //Then
        assertEquals("Success(Hello)", success.toString());
    }

    @Test
    public void convertsFailureToString() {
        //Given-When
        Try<Integer> failure = Try.with(() -> 2 / 0);

        //Then
        final ArithmeticException arithmeticException = new ArithmeticException("/ by zero");
        assertEquals(String.format("Failure(%s)", arithmeticException), failure.toString());
    }

    @Test
    public void successMapsToSuccess() {
        //Given
        String value = "Hello";
        FunctionThrowsException<String, String, Exception> fte = s -> TrySpecsUtil.capitalize(s);
        Try<String> success = Try.with(fte, value);

        //When
        Try<Integer> mappedSuccess = success.map(String::length);

        //Then
        assertEquals(value.length(), mappedSuccess.get().intValue());
    }

    @Test
    public void successMapsToFailure() {
        //Given
        Try<String> success = Try.with(() -> null);

        //When
        Try<Integer> mappedFailure = success.map(TrySpecsUtil::length);

        //Then
        assertTrue(mappedFailure.isFailure());
    }

    @Test
    public void failureMapsToFailure() {
        //Given
        String nothing = null;
        Try<Integer> failure = Try.with(() -> nothing.length());

        //When
        Try<Integer> mappedFailure = failure.map(len -> len * 2);

        //Then
        assertTrue(mappedFailure.isFailure());
    }

    @Test
    public void filtersSuccessWhenPredicateHolds() {
        //Given
        Try<Integer> success = Try.with(() -> 2);

        //When
        Try<Integer> filtered = success.filter(x -> true);

        //Then
        assertEquals(filtered.get(), success.get());
    }

    @Test
    public void successConvertsToFailureWhenPredicateDoesNotHold() {
        //Given
        Try<Integer> success = Try.with(() -> 2);

        //When
        Try<Integer> filtered = success.filter(x -> false);

        assertTrue(filtered.isFailure());
    }

    @Test
    public void filteringFailureAlwaysResultsInFailure() {
        //Given-When
        Try<Integer> failure = Try.with(() -> 2 / 0);

        //When
        Try<Integer> predicateHolds = failure.filter(x -> true);
        Try<Integer> predicateDoesNotHold = failure.filter(x -> false);

        assertTrue(predicateHolds.isFailure());
        assertTrue(predicateDoesNotHold.isFailure());
    }

    @Test
    public void successFlattensToSuccess() {
        //Given
        String value = "Hello";
        Try<String> success = Try.with(() -> value);

        //When
        final String prefix = "=> ";
        Try<String> flattened = success.flatMap(s -> TrySpecsUtil.prefixCapitalize(prefix, s));

        //Then
        assertEquals(prefix + value.toUpperCase(), flattened.get());
    }

    @Test
    public void successFlattensToFailure() {
        //Given
        Try<String> success = Try.with(() -> "Hello");

        //When
        final String prefix = null;
        Try<String> flattened = success.flatMap(s -> TrySpecsUtil.prefixCapitalize(prefix, s));

        //Then
        assertTrue(flattened.isFailure());
    }

    @Test
    public void failureFlattensToFailure() {
        //Given
        Try<String> failure = Try.with(() -> TrySpecsUtil.methodAlwaysThrows());

        //When
        Try<String> flattened = failure.flatMap(s -> TrySpecsUtil.prefixCapitalize("=> ", s));

        //Then
        assertTrue(flattened.isFailure());
    }

    @Test
    public void consumesSuccessValue() {
        //Given
        String value = "Hello";
        Try<String> success = Try.with(() -> value);

        //When
        StringBuilder result = new StringBuilder();
        success.forEach(s -> result.append(s));

        //Then
        assertEquals(value, result.toString());
    }

    @Test
    public void doesNotConsumeFailure() {
        //Given
        Try<String> failure = Try.with(() -> TrySpecsUtil.methodAlwaysThrows());

        //When
        StringBuilder result = new StringBuilder();
        failure.forEach(s -> result.append(s));

        //Then
        assertEquals("", result.toString());
    }

    @Test
    public void successConvertsToOptionalWithValue() {
        //Given
        String value = "Hello";
        Try<String> success = Try.with(() -> value);

        //When
        final Optional<String> optional = success.toOptional();

        //Then
        assertTrue(optional.isPresent());
        assertEquals(value, optional.get());
    }

    @Test
    public void failureConvertsToEmptyOptional() {
        //Given
        Try<String> failure = Try.with(() -> TrySpecsUtil.methodAlwaysThrows());

        //When
        Optional<String> optional = failure.toOptional();

        //Then
        assertFalse(optional.isPresent());
    }

    @Test
    public void recoversFromFailure() {
        //Given
        Try<Double> failure = Try.with(() -> 0d / 0);

        //When
        Try<Double> recovered = failure.recover(t -> Double.NaN);

        //Then
        assertTrue(recovered.isSuccess());
        assertEquals(Double.NaN, recovered.get(), 0);
    }

    @Test
    public void successDoesNotRecover() {
        //Given
        Try<Double> success = Try.with(() -> 8d / 3);

        //When
        Try<Double> success2 = success.recover(t -> Double.NaN);

        //Then
        assertTrue(success2.isSuccess());
        assertSame(success, success2);
    }

}

class TrySpecsUtil {
    private static final Random random = new Random();

    static String methodAlwaysThrows() throws Exception {
        throw new Exception("failure");
    }

    static String tryCapitalize(Throwable t) {
        if (t instanceof IllegalArgumentException) {
            return "";
        }
        return "";
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


    static void printCapitalized(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        System.out.println(s.toUpperCase());

    }

    static boolean gte5(String s) throws Exception {
        if (null == s)
            throw new Exception("null");

        return s.length() < 5 ? false : true;
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
}
