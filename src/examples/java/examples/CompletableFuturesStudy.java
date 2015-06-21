package examples;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//http://www.nurkiewicz.com/2013/05/java-8-definitive-guide-to.html
public class CompletableFuturesStudy {

    private static Random random = new Random();

    static Function<Integer, Integer> expensiveSquare = x -> {
        System.out.println("Now Squaring...");
        try {
            Thread.sleep(2 * 1000);
        } catch(Exception e) {
        }
        System.out.println("Squaring done...");
        return x * x;
    };

    static CompletableFuture<Integer> square(Integer x) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(x * x);
    }

    static CompletableFuture<Integer> cube(Integer x) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return CompletableFuture.completedFuture(x * x * x);
    }

    static CompletableFuture<Integer> slowButPredictableSquare(Integer x) {
        final CompletableFuture<Integer> integerCompletableFuture = new CompletableFuture<>();
        integerCompletableFuture.supplyAsync(() ->
        {
            try {
                Thread.sleep(2 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Returning from slowButPredictableSquare for x = " + x);
            return x * x;
        });

        return integerCompletableFuture;
    }

    static CompletableFuture<Integer> fastButUnpredictableSquare(Integer x) {
        final boolean canRespond = random.nextBoolean();
        final CompletableFuture<Integer> integerCompletableFuture = new CompletableFuture<>();
        if (canRespond) {
            System.out.println("Returning from fastButUnpredictableSquare");
            integerCompletableFuture.complete(x * x);
//            return CompletableFuture.completedFuture(x * x);
        } else {
            System.out.println("Returning from fastButUnpredictableSquare with failure");
            integerCompletableFuture.completeExceptionally(new UnsupportedOperationException("Could not do squaring"));
//            return CompletableFuture.supplyAsync(() -> { throw new UnsupportedOperationException("Could not do squaring"); });
        }
        return integerCompletableFuture;
    }


    public static void main(String[] args) {
//        final CompletableFuture<Void> voidCompletableFuture = CompletableFuture.supplyAsync(() -> expensiveSquare.apply(3))
//                .thenAcceptAsync(System.out::println);

//        System.out.println("Waiting for future to complete...");
//        voidCompletableFuture.join();

        final CompletableFuture<Integer> anotherFuture =
                CompletableFuture.supplyAsync(() -> expensiveSquare.apply(2))
                        .thenApply(squared -> 2 * squared);

        try {
            System.out.println("anotherFuture.get() = " + anotherFuture.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


// Transforming and acting on one CompletableFuture (thenApply)

// So I said that CompletableFuture is superior to Future but you haven't yet seen why?
// Simply put, it's because CompletableFuture is a monad and a functor.
// Not helping I guess? Both Scala and JavaScript allow registering asynchronous
// callbacks when future is completed. We don't have to wait and block until
// it's ready. We can simply say: run this function on a result, when it
// arrives. Moreover, we can stack such functions, combine multiple futures together,
// etc. For example if we have a function from String to Integer we can turn
// CompletableFuture<String> to CompletableFuture<Integer without unwrapping it.
// This is achieved with thenApply() family of methods:
//<U> CompletableFuture<U> thenApply(Function<? super T,? extends U> fn);
//<U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn);
//<U> CompletableFuture<U> thenApplyAsync(Function<? super T,? extends U> fn, Executor executor);
//As stated before ...Async versions are provided for most operations on CompletableFuture thus
// I will skip them in subsequent sections. Just remember that first method will apply function
// within the same thread in which the future completed while the remaining two will apply it
// asynchronously in different thread pool.
//Let's see how thenApply() works:


        CompletableFuture<Double> transformed =
                CompletableFuture.supplyAsync(() -> "2")
                        .thenApply(Integer::parseInt)
                        .thenApply(r -> r * r * Math.PI);

//You see a sequence of transformations here. From String to Integer and then to Double. But what's
// most important, these transformations are neither executed immediately nor blocking. They are simply
// remembered and when original completes they are executed for you. If some of the transformations
// are time-consuming, you can supply your own Executor to run them asynchronously. Notice that
// this operation is equivalent to monadic map in Scala.

// Running code on completion (thenAccept/thenRun)
//
// CompletableFuture<Void> thenAccept(Consumer<? super T> block);
// CompletableFuture<Void> thenRun(Runnable action);
// These two methods are typical "final" stages in future pipeline. They allow you to
// consume future value when it's ready. While thenAccept() provides the final value,
// thenRun executes Runnable which doesn't even have access to computed value.

        transformed.thenAccept(System.out::println);


// Error handling of single CompletableFuture
// So far we only talked about result of computation. But what about exceptions? Can we handle
// them asynchronously as well? Sure!

        System.out.println("Failure Recovery");

//        CompletableFuture<Double> safe = CompletableFuture.supplyAsync(() -> 2d/0);
        CompletableFuture<Double> safe = CompletableFuture.supplyAsync(() -> 4d/2);
        CompletableFuture<?> recovered = safe.exceptionally(ex -> Double.NaN);
        recovered.thenAccept(System.out::println);

// exceptionally() takes a function that will be invoked when original future
// throws an exception. We then have an opportunity to recover by transforming this
// exception into some value compatible with Future's type. Further transformations
// of safe will no longer yield an exception but instead a Double returned from
// supplied function.

//A more flexible approach is handle() that takes a function receiving either correct
// result or exception:

     System.out.println("Recovery using handle()");
//     CompletableFuture.supplyAsync(() -> 4d/2)
     CompletableFuture.supplyAsync(() -> 4d/0)
             .handle((result, ex) -> {
                 if (result != null) {
                     return result;
                 } else {
                     return Double.NaN;
                 }
             })
             .thenAccept(System.out::println);

//handle() is called always, with either result or exception argument being not-null. This
// is a one-stop catch-all strategy.

// Combining two CompletableFuture together

// Asynchronous processing of one CompletableFuture is nice but it really shows
// its power when multiple such futures are combined together in various ways.

// Combining (chaining) two futures (thenCompose())
// Sometimes you want to run some function on future's value (when it's ready).
// But this function returns future as well. CompletableFuture should be smart enough to
// understand that the result of our function should now be used as top-level future, as
// opposed to CompletableFuture<CompletableFuture<T>>. Method thenCompose() is thus
// equivalent to flatMap in Scala:
//
// <U> CompletableFuture<U> thenCompose(Function<? super T,CompletableFuture<U>> fn);

//...Async variations are available as well. Example below, look carefully at the types and
// the difference between thenApply() (map) and thenCompose() (flatMap)
    System.out.println("Squaring and then Cubing...");
    square(2).thenCompose(result -> cube(result)).thenAccept(System.out::println);

    System.out.println("Cubing and then Squaring...");
    cube(3).thenCompose(result -> square(result)).thenAccept(System.out::println);

// Transforming values of two futures (thenCombine())
// While thenCompose() is used to chain one future dependent on the other, thenCombine
// combines two independent futures when they are both done:
        long startTime = System.currentTimeMillis();
        final CompletableFuture<Integer> firstFuture = CompletableFuture.supplyAsync(() -> expensiveSquare.apply(2));

        final CompletableFuture<Integer> secondFuture = CompletableFuture.supplyAsync(() ->
                expensiveSquare.apply(5));
        final CompletableFuture<Integer> combinedFuture = firstFuture
                .thenCombine(secondFuture, (res1, res2) -> res1 + res2);

// Waiting for both CompletableFutures to complete

// If instead of producing new CompletableFuture combining both results we simply want to
// be notified when they finish, we can use thenAcceptBoth()/runAfterBoth() family of methods
// (...Async variations are available as well). They work similarly to thenAccept() and thenRun()
// but wait for two futures instead of one:
// <U> CompletableFuture<Void> thenAcceptBoth(CompletableFuture<? extends U> other, BiConsumer<? super T,? super U> block)
// CompletableFuture<Void> runAfterBoth(CompletableFuture<?> other, Runnable action)
// I hope I'm wrong but maybe some of you are asking themselves a question: why can't I simply block on these two futures?
// Well, of course you can. But the whole point of CompletableFuture is to allow asynchronous, event
// driven programming model instead of blocking and eagerly waiting for result. So functionally two
// code snippets above are equivalent, but when waiting for results unnecessarily occupies one thread
// of execution.
        combinedFuture.thenAcceptAsync(System.out::println);

        combinedFuture.join();
        long timeTaken = System.currentTimeMillis() - startTime;
        System.out.println("sumOf 2 Squares = took " + timeTaken + " ms.");

// Waiting for first CompletableFuture to complete
// Another interesting part of the CompletableFuture API is the ability to wait for first
// (as opposed to all) completed future. This can come handy when you have two tasks yielding result
// of the same type and you only care about response time, not which task resulted first. API
// methods (...Async variations are available as well):
// CompletableFuture<Void> acceptEither(CompletableFuture<? extends T> other, Consumer<? super T> block)
// CompletableFuture<Void> runAfterEither(CompletableFuture<?> other, Runnable action)
// As an example say you have two systems you integrate with.
// One has smaller average response times but high standard deviation.
// Other one is slower in general, but more predictable.
// In order to take best of both worlds (performance and predictability) you call
// both systems at the same time and wait for the first one to complete.
// Normally it will be the first one, but in case it became slow, second one finishes
// in an acceptable time:
        System.out.println("Any of the 2 futures that completes first");
        slowButPredictableSquare(2).acceptEither(fastButUnpredictableSquare(2), System.out::println);

// Transforming first completed

// applyToEither() is an older brother of acceptEither(). While the latter simply calls some
// piece of code when faster of two futures complete, applyToEither() will return a new future.
// This future will complete when first of the two underlying futures complete. API is a bit
// similar (...Async variations are available as well):
// <U> CompletableFuture<U> applyToEither(CompletableFuture<? extends T> other, Function<? super T,U> fn)

// The extra fn function is invoked on the result of first future that completed. I am
// not really sure what's the purpose of such a specialized method, after all one could
// simply use: fast.applyToEither(predictable).thenApply(fn). Since we are stuck with this
// API but we don't really need extra function application, I will simply use a doubler lambda.
        System.out.println("Transforming first completed future...");
        fastButUnpredictableSquare(3)
                .applyToEither(slowButPredictableSquare(3), result -> 2 * result)
                .thenAccept(System.out::println);

// Combining multiple CompletableFuture together
// So we now know how to wait for two futures to complete (using thenCombine())
// and for the first one to complete (applyToEither()). But can it scale to arbitrary
// number of futures? Sure, using static helper methods:
// static CompletableFuture<Void> allOf(CompletableFuture<?>... cfs)
// static CompletableFuture<Object> anyOf(CompletableFuture<?>... cfs)
// allOf() takes an array of futures and returns a future that completes when all of the
// underlying futures are completed (barrier waiting for all). anyOf() on the other
// hand will wait only for the fastest of the underlying futures. Please look at the
// generic type of returned futures. Not quite what you would expect?

   System.out.println("Array of futures...");

   final CompletableFuture<Integer>[] futures =
           new CompletableFuture [] {
            slowButPredictableSquare(1),
           slowButPredictableSquare(2),
           slowButPredictableSquare(3),
           slowButPredictableSquare(4)};

   System.out.println("Waiting for all futures to complete");
   final CompletableFuture<Void> allOf = CompletableFuture.allOf(futures);
//   allOf.join();

   System.out.println("All futures Complete");

        final CompletableFuture<Integer>[] anotherFutures =
                new CompletableFuture [] {
                        slowButPredictableSquare(5),
                        slowButPredictableSquare(6),
                        slowButPredictableSquare(7),
                        slowButPredictableSquare(8)};

        System.out.println("Waiting for all futures to complete");
        final CompletableFuture<?> anyOf = CompletableFuture.anyOf(anotherFutures);
        anyOf.thenAccept(result -> {
            System.out.println("Completed any 1 future, result = " + result);
        });

// Squares of following numbers using expensive square
        final ExecutorService executorService = Executors.newWorkStealingPool(6);
//        final ExecutorService executorService = Executors.newWorkStealingPool(4);
        startTime = System.currentTimeMillis();
        final Integer sumOfSquares = Arrays.asList(1, 2, 3, 4, 5, 6)
//                .stream()
                .parallelStream()
//                   .map(expensiveSquare)
                .map(n -> CompletableFuture.supplyAsync(() -> expensiveSquare.apply(n), executorService))
                .map(fut -> fut.join())
                .reduce(0, (acc, elem) -> acc + elem);
//
        timeTaken = System.currentTimeMillis() - startTime;
        System.out.println("sumOfSquares = " + sumOfSquares + " [took " + timeTaken + " ms.]");


    }
}
