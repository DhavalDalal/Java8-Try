package com.tsys.utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class TrySpecs {
    
    private static Supplier<Integer> divisionByZero = () -> 2 / 0;

    @Test
    public void successCreatedFromSupplierThrowingCheckedException() {
        //Given
        SupplierThrowsException<Integer, Exception> ste = () -> 2;

        //When
        Try<Integer> success = Try.with(ste);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
    }

    @Test
    public void retrievesSuccessValueFromSupplierThrowingCheckedException() {
        //Given
        Integer value = 2;
        SupplierThrowsException<Integer, Exception> ste = () -> value;

        //When
        Try<Integer> success = Try.with(ste);

        //Then
        assertEquals(value, success.get());
    }

    @Test
    public void failureCreatedWhenSupplierThrowsUncheckedException() {
        //Given-When
        Try<Integer> failure = Try.with(divisionByZero);

        //Then
        assertTrue(failure.isFailure());
    }

    @Test
    public void shoutsWhenRetrievingValueFromFailureFromSupplierThrowingException() {
        //Given-When
        Try<Integer> failure = Try.with(divisionByZero);

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
    public void successCreatedFromFunctionThrowingUncheckedException() {
        //Given
        String value = "Hello";
        Function<String, String> capitalize = s -> {
            if (null == s)
                throw new IllegalArgumentException("empty string");

            return s.toUpperCase();
        };

        //When
        Try<String> success = Try.with(capitalize, value);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
    }

    @Test
    public void successCreatedFromConsumerThrowingException() {
        //Given
        final String value = "Hello";
        final StringBuilder result = new StringBuilder();
        ConsumerThrowsException<String, Exception> cte =
            s -> {
                if (null == s)
                    throw new Exception("null received");

                result.append(s);
            };

        //When
        Try<Void> success = Try.with(cte, value);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
        assertEquals(value, result.toString());
    }

    @Test
    public void retrievesNoSuccessValueFromConsumerThrowingException() {
        //Given
        ConsumerThrowsException<String, Exception> cte =
                s -> {
                    if (null == s)
                        throw new Exception("null received");
                };

        //When
        Try<Void> success = Try.with(cte, "Hello");

        //Then
        assertEquals(null, success.get());
    }

    @Test
    public void successCreatedFromConsumerThrowingUncheckedException() {
        //Given
        final String value = "Hello";
        final StringBuilder result = new StringBuilder();
        Consumer<String> consumer =
                s -> {
                    if (null == s)
                        throw new IllegalArgumentException("null received");

                    result.append(s);
                };

        //When
        Try<Void> success = Try.with(consumer, value);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
        assertEquals(value, result.toString());
    }


    @Test
    public void failureCreatedWhenConsumerThrowsException() {
        //Given
        ConsumerThrowsException<String, Exception> cte =
                s -> { throw new Exception("on purpose"); };

        //When
        Try<Void> failure = Try.with(cte, "Hello");

        //Then
        assertTrue(failure.isFailure());
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
    public void failureCreatedWhenPredicateThrowsException() {
        //Given
        PredicateThrowsException<String, Exception> pte =
                s -> { throw new Exception("on purpose"); };

        //When
        Try<String> failure = Try.with(pte, "Hello");

        //Then
        assertTrue(failure.isFailure());
    }

    @Test
    public void successCreatedFromPredicateThrowingUncheckedException() {
        //Given
        final String value = "Hello";
        Predicate<String> pte = s -> {
            if (null == s)
                throw new IllegalArgumentException("empty string");

            return s.length() >= 5;
        };

        //When
        Try<String> success = Try.with(pte, value);

        //Then
        assertTrue(success.isSuccess());
        assertFalse(success.isFailure());
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
        Try<Integer> failure = Try.with(divisionByZero);

        //Then
        ArithmeticException arithmeticException = new ArithmeticException("/ by zero");
        assertEquals(String.format("Failure(%s)", arithmeticException), failure.toString());
    }

    @Test
    public void successMapsToSuccess() {
        //Given
        String value = "Hello";
        Function<String, String> fte = String::toUpperCase;
        Try<String> success = Try.with(fte, value);

        //When
        Try<Integer> mappedSuccess = success.map(String::length);

        //Then
        assertEquals(value.length(), mappedSuccess.get().intValue());
    }

    @Test
    public void successMapsToFailure() {
        //Given
        Supplier<String> empty = () -> null;
        Try<String> success = Try.with(empty);

        //When
        Try<Integer> mappedFailure = success.map(String::length);

        //Then
        assertTrue(mappedFailure.isFailure());
    }

    @Test
    public void failureMapsToFailure() {
        //Given
        final String nothing = null;
        Supplier<Integer> empty = () -> nothing.length();
        Try<Integer> failure = Try.with(empty);

        //When
        Try<Integer> mappedFailure = failure.map(len -> len * 2);

        //Then
        assertTrue(mappedFailure.isFailure());
    }

    @Test
    public void filtersSuccessWhenPredicateHolds() {
        //Given
        Supplier<Integer> supplier = () -> 2;
        Try<Integer> success = Try.with(supplier);

        //When
        Try<Integer> filtered = success.filter(x -> true);

        //Then
        assertEquals(filtered.get(), success.get());
    }

    @Test
    public void successConvertsToFailureWhenPredicateDoesNotHold() {
        //Given
        Supplier<Integer> supplier = () -> 2;
        Try<Integer> success = Try.with(supplier);

        //When
        Try<Integer> filtered = success.filter(x -> false);

        assertTrue(filtered.isFailure());
    }

    @Test
    public void filteringFailureAlwaysResultsInFailure() {
        //Given-When
        Try<Integer> failure = Try.with(divisionByZero);

        //When
        Try<Integer> predicateHolds = failure.filter(x -> true);
        Try<Integer> predicateDoesNotHold = failure.filter(x -> false);

        assertTrue(predicateHolds.isFailure());
        assertTrue(predicateDoesNotHold.isFailure());
    }

    @Test
    public void successFlattensToSuccess() {
        //Given
        final String value = "Hello";
        Supplier<String> supplier = () -> value;
        Try<String> success = Try.with(supplier);

        //When
        final String prefix = "=> ";
        Try<String> flattened = success.flatMap(s -> TrySpecsUtil.prefixCapitalize(prefix, s));

        //Then
        assertEquals(prefix + value.toUpperCase(), flattened.get());
    }

    @Test
    public void successFlattensToFailure() {
        //Given
        Supplier<String> supplier = () -> "Hello";
        Try<String> success = Try.with(supplier);

        //When
        final String prefix = null;
        Try<String> flattened = success.flatMap(s -> TrySpecsUtil.prefixCapitalize(prefix, s));

        //Then
        assertTrue(flattened.isFailure());
    }

    @Test
    public void failureFlattensToFailure() {
        //Given
        SupplierThrowsException<String, Exception> supplier =
                () -> TrySpecsUtil.methodAlwaysThrows();
        Try<String> failure = Try.with(supplier);

        //When
        Try<String> flattened = failure.flatMap(s -> TrySpecsUtil.prefixCapitalize("=> ", s));

        //Then
        assertTrue(flattened.isFailure());
    }

    @Test
    public void consumesSuccessValue() {
        //Given
        String value = "Hello";
        Supplier<String> supplier = () -> value;
        Try<String> success = Try.with(supplier);

        //When
        StringBuilder result = new StringBuilder();
        success.forEach(s -> result.append(s));

        //Then
        assertEquals(value, result.toString());
    }

    @Test
    public void doesNotConsumeFailure() {
        //Given
        SupplierThrowsException<String, Exception> ste = () -> TrySpecsUtil.methodAlwaysThrows();
        Try<String> failure = Try.with(ste);

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
        Supplier<String> supplier = () -> value;
        Try<String> success = Try.with(supplier);

        //When
        final Optional<String> optional = success.toOptional();

        //Then
        assertTrue(optional.isPresent());
        assertEquals(value, optional.get());
    }

    @Test
    public void failureConvertsToEmptyOptional() {
        //Given
        SupplierThrowsException<String, Exception> ste = () -> TrySpecsUtil.methodAlwaysThrows();
        Try<String> failure = Try.with(ste);

        //When
        Optional<String> optional = failure.toOptional();

        //Then
        assertFalse(optional.isPresent());
    }

    @Test
    public void recoversFromFailure() {
        //Given
        SupplierThrowsException<String, Exception> ste = () -> TrySpecsUtil.methodAlwaysThrows();
        Try<String> failure = Try.with(ste);

        //When
        String value = "";
        Try<String> recovered = failure.recover(t -> value);

        //Then
        assertTrue(recovered.isSuccess());
        assertEquals(value, recovered.get());
    }

    @Test
    public void successDoesNotRecover() {
        //Given
        Supplier<Double> division = () -> 8d / 3;
        Try<Double> success = Try.with(division);

        //When
        Try<Double> success2 = success.recover(t -> Double.NaN);

        //Then
        assertTrue(success2.isSuccess());
        assertSame(success, success2);
    }

    @Test
    public void flattenedRecoveryFromFailure() {
        //Given
        SupplierThrowsException<String, Exception> ste = () -> TrySpecsUtil.methodAlwaysThrows();
        Try<String> failure = Try.with(ste);

        //When
        String value = "";
        Supplier<String> supplier = () -> value;
        Try<String> flattenedRecovery = failure.recoverWith(t -> Try.with(supplier));

        //Then
        assertTrue(flattenedRecovery.isSuccess());
        assertEquals(value, flattenedRecovery.get());
    }

    @Test
    public void successDoesNotRecoverAfterFlattening() {
        //Given
        Supplier<Double> division = () -> 8d / 3;
        Try<Double> success = Try.with(division);

        //When
        Supplier<Double> defaultSupplier = () -> Double.NaN;
        Try<Double> success2 = success.recoverWith(t -> Try.with(defaultSupplier));

        //Then
        assertTrue(success2.isSuccess());
        assertSame(success, success2);
    }

    @Test
    public void failsASuccess() {
        //Given
        Supplier<Double> division = () -> 8d / 3;
        Try<Double> success = Try.with(division);

        //When
        Try<Double> failed = success.failed();

        //Then
        assertTrue(failed.isFailure());
    }

    @Test
    public void succeedsAFailure() {
        //Given
        Try<Integer> failure = Try.with(divisionByZero);

        //When
        Try<Integer> success = failure.failed();

        //Then
        assertTrue(success.isSuccess());
    }

    @Test
    public void transformsASuccess() {
        //Given
        Supplier<Double> division = () -> 8d / 2;
        Try<Double> success = Try.with(division);

        Function<Double, Try<Double>> successFn = x -> Try.with((Supplier<Double>) () -> x * 2.0);

        Function<Throwable, Try<Double>> failureFn = t -> Try.with((Supplier<Double>) () -> Double.NaN);

        //When
        Try<Double> transformed = success.transform(successFn, failureFn);

        //Then
        assertEquals(8.0d, transformed.get(), 0.0001);
    }

    @Test
    public void transformsAFailure() {
        //Given
        Try<Integer> failure = Try.with(divisionByZero);

        Function<Integer, Try<Double>> successFn = x -> Try.with((Supplier<Double>) () -> x * 2.0);

        Function<Throwable, Try<Double>> failureFn = t -> Try.with((Supplier<Double>) () -> Double.NaN);

        //When
        Try<Double> transformed = failure.transform(successFn, failureFn);

        //Then
        assertEquals(Double.NaN, transformed.get(), 0.0);
    }

    @Test
    public void failureDefaultsToAValue() {
        //Given
        Try<Integer> failure = Try.with(divisionByZero);

        //When
        final int defaultValue = 2;
        int value = failure.getOrElse(defaultValue);

        //Then
        assertEquals(defaultValue, value);
    }

    @Test
    public void successDoesNotDefaultToAValue() {
        //Given
        Try<Integer> success = Try.with((Supplier<Integer>) () -> 2 / 2);

        //When
        final int defaultValue = 2;
        int value = success.getOrElse(defaultValue);

        //Then
        assertEquals(1, value);
    }


    @Test
    public void failureDefaultsToAnotherTry() {
        //Given
        Try<Integer> failure = Try.with(divisionByZero);

        //When
        Try<Integer> defaultValue = failure.orElse(Try.with((Supplier<Integer>) () -> 2 / 1));

        //Then
        assertEquals(2, defaultValue.get().intValue());
    }

    @Test
    public void flattensANestedSuccess() {
        //Given
        Try<Integer> success = Try.with((Supplier<Integer>) () -> 8 / 2);
        Try<Try<Integer>> nestedSuccess = Try.with((Supplier<Try<Integer>>) () -> success);

        //When
        Try<Integer> flattenedSuccess = nestedSuccess.flatten();

        //Then
        assertEquals(success.get(), flattenedSuccess.get());

    }

    @Test
    public void flattensANestedFailure() {
        //Given
        
        Try<Integer> failure = Try.with(divisionByZero);
        Try<Try<Integer>> nestedFailure = Try.with((Supplier<Try<Integer>>) () -> failure);

        //When
        Try<Integer> flattenedFailure = nestedFailure.flatten();


        //Then
        assertEquals(failure.get(), flattenedFailure.get());
    }
}

