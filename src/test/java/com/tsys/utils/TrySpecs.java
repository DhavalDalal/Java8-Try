package com.tsys.utils;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

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
        Try<String> failure = Try.with(() -> TrySpecsUtil.methodAlwaysThrows());

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
        Try<Double> success = Try.with(() -> 8d / 3);

        //When
        Try<Double> success2 = success.recover(t -> Double.NaN);

        //Then
        assertTrue(success2.isSuccess());
        assertSame(success, success2);
    }

    @Test
    public void flattenedRecoveryFromFailure() {
        //Given
        Try<String> failure = Try.with(() -> TrySpecsUtil.methodAlwaysThrows());
        assertTrue(failure.isFailure());

        //When
        String value = "";
        Try<String> flattenedRecovery = failure.recoverWith(t -> Try.with(() -> value));

        //Then
        assertTrue(flattenedRecovery.isSuccess());
        assertEquals(value, flattenedRecovery.get());
    }

    @Test
    public void successDoesNotRecoverAfterFlattening() {
        //Given
        Try<Double> success = Try.with(() -> 8d / 3);

        //When
        Try<Double> success2 = success.recoverWith(t -> Try.with(() -> Double.NaN));

        //Then
        assertTrue(success2.isSuccess());
        assertSame(success, success2);
    }

    @Test
    public void failsASuccess() {
        //Given
        Try<Double> success = Try.with(() -> 8d / 3);

        //When
        Try<Double> failed = success.failed();

        //Then
        assertTrue(failed.isFailure());
    }

    @Test
    public void succeedsAFailure() {
        //Given
        Try<Integer> failure = Try.with(() -> 2 / 0);

        //When
        Try<Integer> success = failure.failed();

        //Then
        assertTrue(success.isSuccess());
    }

    @Test
    public void transformsASuccess() {
        //Given
        Try<Integer> success = Try.with(() -> 8 / 2);

        Function<Integer, Try<Double>> successFn = x -> Try.with(() -> x * 2.0);

        Function<Throwable, Try<Double>> failureFn = t -> Try.with(() -> Double.NaN);

        //When
        Try<Double> transformed = success.transform(successFn, failureFn);

        //Then
        assertEquals(8.0d, transformed.get(), 0.0001);
    }

    @Test
    public void transformsAFailure() {
        //Given
        Try<Integer> failure = Try.with(() -> 2 / 0);

        Function<Integer, Try<Double>> successFn = x -> Try.with(() -> x * 2.0);

        Function<Throwable, Try<Double>> failureFn = t -> Try.with(() -> Double.NaN);

        //When
        Try<Double> transformed = failure.transform(successFn, failureFn);

        //Then
        assertEquals(Double.NaN, transformed.get(), 0.0);
    }

    @Test
    public void failureDefaultsToAValue() {
        //Given
        Try<Integer> failure = Try.with(() -> 2 / 0);

        //When
        final int defaultValue = 2;
        int value = failure.getOrElse(defaultValue);

        //Then
        assertEquals(defaultValue, value);
    }

    @Test
    public void successDoesNotDefaultToAValue() {
        //Given
        Try<Integer> success = Try.with(() -> 2 / 2);

        //When
        final int defaultValue = 2;
        int value = success.getOrElse(defaultValue);

        //Then
        assertEquals(1, value);
    }


    @Test
    public void failureDefaultsToAnotherTry() {
        //Given
        Try<Integer> failure = Try.with(() -> 2 / 0);

        //When
        Try<Integer> defaultValue = failure.orElse(Try.with(() -> 2 / 1));

        //Then
        assertEquals(2, defaultValue.get().intValue());
    }
}

